package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.enums.AdvertisementStatus;
import org.marketplace.models.Advertisement;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.marketplace.repositories.CategoryManagementRepository;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.requests.UserNotFoundException;
import org.marketplace.specifications.AdvertisementSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdvertisementManagementService {
    private final AdvertisementManagementRepository advertisementManagementRepository;
    private final UserManagementRepository userManagementRepository;
    private final Logger LOGGER = LogManager.getLogger(AdvertisementManagementService.class);
    private final CategoryManagementRepository categoryManagementRepository;
    @Autowired
    EmailService emailService;


    public AdvertisementManagementService(AdvertisementManagementRepository advertisementManagementRepository, EmailService emailService, UserManagementRepository userManagementRepository, CategoryManagementRepository categoryManagementRepository) {
        this.advertisementManagementRepository = advertisementManagementRepository;
        this.emailService = emailService;
        this.userManagementRepository = userManagementRepository;
        this.categoryManagementRepository = categoryManagementRepository;
    }

    public Advertisement addAdvertisement(Advertisement advertisement) {
        Long advertisementId = advertisement.getId();
        if (advertisement.getUser() == null)
            throw new UserNotFoundException("User can not be null");
        if (advertisement.getCategory() == null)
            throw new UserNotFoundException("Category can not be null");
        Long userId = advertisement.getUser().getId();
        Long categoryId = advertisement.getCategory().getId();
        if (!userManagementRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User with id: %d not exists!", userId));
        if (!categoryManagementRepository.existsById(categoryId))
            throw new EntityNotFoundException(String.format("Category with id: %d not exists!", categoryId));
        if (advertisementId == null)
            return advertisementManagementRepository.save(advertisement);
        try {
            getAdvertisementById(advertisementId);
            LOGGER.error("Ads " + advertisement.getTitle() + " already exists.");
            throw new EntityExistsException(String.format("Advertisement with id: %d already exists!", advertisementId));
        } catch (EntityNotFoundException e) {
            LOGGER.info("Ads " + advertisement.getTitle() + " has been added.");
            return advertisementManagementRepository.save(advertisement);
        }
    }

    public Advertisement updateAdvertisement(Advertisement advertisement) {
        try {
            Advertisement ad = getAdvertisementById(advertisement.getId());
            if (ad.getPrice() != advertisement.getPrice()) {
                emailService.sendEmailToObservers(ad.getObservers(), advertisement.getPrice(), advertisement.getTitle());
            }

            return advertisementManagementRepository.save(advertisement);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", advertisement.getId()));
        }
    }

    public void deleteAdvertisement(Long id) {
        if (!advertisementManagementRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id));
        }

        Advertisement ad = getAdvertisementById(id);
        emailService.sendEmailToObservers(ad.getObservers(), AdvertisementStatus.DELETED, ad.getTitle());
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

    public Advertisement buyAdvertisement(Long id, Long currentUserId) {
        Advertisement ad = getAdvertisementById(id);
        ad.setStatus(AdvertisementStatus.BOUGHT);
        ad.setBuyerId(currentUserId);

        emailService.sendEmailToOwner(ad.getUser(), ad, ad.getUser().getEmail());
        emailService.sendEmailToObservers(ad.getObservers(), AdvertisementStatus.BOUGHT, ad.getTitle());

        return advertisementManagementRepository.save(ad);
    }

    public Advertisement observeAdvertisement(Long id, String email) {
        Advertisement ad = getAdvertisementById(id);
        if (ad.getObservers() == null) {
            ad.setObservers(List.of());
        }

        List<String> currentObservers = ad.getObservers();

        if (currentObservers.contains(email)) {
            throw new EntityExistsException(String.format("User with email: %s already observes the advertisement with id: %d", email, id));
        }

        ad.getObservers().add(email);
        return advertisementManagementRepository.save(ad);
    }

    public Advertisement unobserveAdvertisement(Long id, String email) {
        Advertisement ad = getAdvertisementById(id);
        List<String> currentObservers = ad.getObservers();

        if (!currentObservers.contains(email)) {
            throw new EntityNotFoundException(String.format("User with email: %s does not observe the advertisement with id: %d", email, id));
        }

        ad.getObservers().remove(email);
        return advertisementManagementRepository.save(ad);
    }

    public Advertisement changeAdvertisementStatus(Long id, String status) {
        if (status.isEmpty() || (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("BOUGHT") && !status.equals("DELETED"))) {
            throw new IllegalArgumentException("Invalid status provided");
        }

        Advertisement ad = getAdvertisementById(id);
        ad.setStatus(AdvertisementStatus.valueOf(status));
        emailService.sendEmailToObservers(ad.getObservers(), AdvertisementStatus.valueOf(status), ad.getTitle());
        return advertisementManagementRepository.save(ad);
    }

    public List<Advertisement> searchAdvertisements(Map<String, String> searchParams) {
        Specification<Advertisement> specification = Specification.where(null);

        for (Map.Entry<String, String> searchEntry : searchParams.entrySet()) {
            specification = specification.and(new AdvertisementSpecification(searchEntry.getKey(), ":", searchEntry.getValue()));
        }

        return advertisementManagementRepository.findAll(specification);
    }
}
