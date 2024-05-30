package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.builders.EmailBuilder;
import org.marketplace.enums.AdvertisementStatus;
import org.marketplace.models.Advertisement;
import org.marketplace.models.Email;
import org.marketplace.models.User;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementManagementService {
    private final AdvertisementManagementRepository advertisementManagementRepository;

    @Autowired
    EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementManagementService.class);

    public AdvertisementManagementService(AdvertisementManagementRepository advertisementManagementRepository, EmailService emailService) {
        this.advertisementManagementRepository = advertisementManagementRepository;
        this.emailService = emailService;
    }

    public Advertisement addAdvertisement(Advertisement advertisement) {
        try {
            getAdvertisementById(advertisement.getId());
            throw new EntityExistsException(String.format("Advertisement with id: %d already exsits!", advertisement.getId()));
        } catch (EntityNotFoundException e) {
            return advertisementManagementRepository.save(advertisement);
        }
    }

    public Advertisement updateAdvertisement(Advertisement advertisement) {
        try {
            getAdvertisementById(advertisement.getId());
            return advertisementManagementRepository.save(advertisement);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", advertisement.getId()));
        }
    }

    public void deleteAdvertisement(Long id) {
        if (!advertisementManagementRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id));
        }
        advertisementManagementRepository.deleteById(id);
    }

    public List<Advertisement> getAllAdvertisements() {
        return advertisementManagementRepository.findAll();
    }

    public Advertisement getAdvertisementById(Long id) {
        Optional<Advertisement> advertisement = advertisementManagementRepository.findById(id);
        if (advertisement.isEmpty()) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id));
        }

        return advertisement.get();
    }

    public List<Advertisement> getAdvertisementsByUser(Long id) {
        return advertisementManagementRepository.findByUser_Id(id);
    }

    public List<Advertisement> getAdvertisementsByCategory(Long id) {
        return advertisementManagementRepository.findByCategory_Id(id);
    }

    public Advertisement buyAdvertisement(Long id, Long currentUserId){
        Advertisement ad = getAdvertisementById(id);
        ad.setStatus(AdvertisementStatus.BOUGHT);
        ad.setBuyerId(currentUserId);

        sendEmailToOwner(ad.getUser(), ad, ad.getUser().getEmail());
        sendEmailToObservers(ad.getObservers(), AdvertisementStatus.BOUGHT, ad.getTitle());

        return advertisementManagementRepository.save(ad);
    }

    public Advertisement observeAdvertisement(Long id, String email){
        Advertisement ad = getAdvertisementById(id);
        if(ad.getObservers() == null){
            ad.setObservers(List.of());
        }

        List<String> currentObservers = ad.getObservers();

        if(currentObservers.contains(email)){
            throw new EntityExistsException(String.format("User with email: %s already observes the advertisement with id: %d", email, id));
        }

        ad.getObservers().add(email);
        return advertisementManagementRepository.save(ad);
    }

    public Advertisement unobserveAdvertisement(Long id, String email){
        Advertisement ad = getAdvertisementById(id);
        List<String> currentObservers = ad.getObservers();

        if(!currentObservers.contains(email)){
            throw new EntityNotFoundException(String.format("User with email: %s does not observe the advertisement with id: %d", email, id));
        }

        ad.getObservers().remove(email);
        return advertisementManagementRepository.save(ad);
    }

    public Advertisement changeAdvertisementStatus(Long id, String status){
        if(status.isEmpty() || (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("BOUGHT") && !status.equals("DELETED"))){
            throw new IllegalArgumentException("Invalid status provided");
        }

        Advertisement ad = getAdvertisementById(id);
        ad.setStatus(AdvertisementStatus.valueOf(status));
        sendEmailToObservers(ad.getObservers(), AdvertisementStatus.valueOf(status), ad.getTitle());
        return advertisementManagementRepository.save(ad);
    }

    private void sendEmailToObservers(List<String> observers, AdvertisementStatus status, String adTitle){
        for(String observer: observers){
            switch(status){
                case ACTIVE:
                    sendActiveEmail(observer, adTitle);
                    break;
                case INACTIVE:
                    sendInactiveEmail(observer, adTitle);
                    break;
                case BOUGHT:
                    sendBoughtEmail(observer, adTitle);
                    break;
                case DELETED:
                    sendDeletedEmail(observer, adTitle);
                    break;
            }
        }
    }

    private void sendEmailToOwner(User user, Advertisement ad, String buyer){
        Email ownerMailMessage = new EmailBuilder()
                .setTo(user.getEmail())
                .setSubject("Your advertisement has been bought!")
                .setContent("Hello " + user.getLogin() + ",\n\nYour advertisement with title: " + ad.getTitle() + " has been bought by user: " + buyer)
                .build();

        emailService.sendEmail(ownerMailMessage);
    }

    private void sendActiveEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "is now active!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing is now active and available for purchase.")
                .build();

        emailService.sendEmail(mailMessage);
    }

    private void sendInactiveEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement" + adTitle + " is now inactive!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing is now inactive and no longer available for purchase.")
                .build();

        emailService.sendEmail(mailMessage);
    }

    private void sendBoughtEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "has been bought!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing has been bought and is no longer available.")
                .build();

        emailService.sendEmail(mailMessage);
    }

    private void sendDeletedEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "has been deleted!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing has been deleted and is no longer available.")
                .build();

        emailService.sendEmail(mailMessage);
    }
}
