package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.Advertisement;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementManagementService {
    private final AdvertisementManagementRepository advertisementManagementRepository;

    public AdvertisementManagementService(AdvertisementManagementRepository advertisementManagementRepository) {
        this.advertisementManagementRepository = advertisementManagementRepository;
    }

    public Advertisement addAdvertisement(Advertisement advertisement) {
        try {
            getAdvertisementById(advertisement.getId());
            throw new EntityExistsException(String.format("Advertisement with id: %d already exsits!", advertisement.getId()));
        } catch(EntityNotFoundException e) {
            return advertisementManagementRepository.save(advertisement);
        }
    }

    public Advertisement updateAdvertisement(Advertisement advertisement) {
        try {
            Advertisement ad = getAdvertisementById(advertisement.getId());
            return advertisementManagementRepository.save(advertisement);
        } catch(EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", advertisement.getId()));
        }
    }

    public void deleteAdvertisement(Long id) {
        if(!advertisementManagementRepository.existsById(id)){
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id));
        }

        advertisementManagementRepository.deleteById(id);
    }

    public List<Advertisement> getAllAdvertisements() {
        return advertisementManagementRepository.findAll();
    }

    public Advertisement getAdvertisementById(Long id) {
        Optional<Advertisement> advertisement = advertisementManagementRepository.findById(id);
        if(advertisement.isEmpty()){
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
}
