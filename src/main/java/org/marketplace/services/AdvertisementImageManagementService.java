package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementImageManagementService {
    private final AdvertisementImageManagementRepository advertisementImageManagementRepository;

    public AdvertisementImageManagementService(AdvertisementImageManagementRepository advertisementImageManagementRepository) {
        this.advertisementImageManagementRepository = advertisementImageManagementRepository;
    }

    public AdvertisementImage addImage(AdvertisementImage advertisementImage) {
        try {
            getAdvertisementImageById(advertisementImage.getId());
            throw new EntityExistsException(String.format("Advertisement image with id: %d already exists!", advertisementImage.getId()));
        } catch (EntityNotFoundException e) {
            return advertisementImageManagementRepository.save(advertisementImage);
        }
    }

    public AdvertisementImage getAdvertisementImageById(Long id) {
        return advertisementImageManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", id)));
    }

    public AdvertisementImage updateAdvertisementImage(AdvertisementImage advertisementImage) {
        return advertisementImageManagementRepository.findById(advertisementImage.getId())
                .map(c -> advertisementImageManagementRepository.save(advertisementImage))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", advertisementImage.getId())));
    }

    public void deleteAdvertisementImage(Long id) {
        if (!advertisementImageManagementRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", id));
        }

        advertisementImageManagementRepository.deleteById(id);
    }

    public List<AdvertisementImage> getAllAdvertisementImages(Long advertisementId) {
        return advertisementImageManagementRepository.findByAdvertisement_Id(advertisementId);
    }
}
