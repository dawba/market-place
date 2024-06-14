package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.marketplace.requests.AdvertisementNotFoundException;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class AdvertisementImageManagementService {
    private final AdvertisementImageManagementRepository advertisementImageManagementRepository;
    private final Logger LOGGER = LogManager.getLogger(AdvertisementImageManagementService.class);
    private final AdvertisementManagementRepository advertisementManagementRepository;

    public AdvertisementImageManagementService(AdvertisementImageManagementRepository advertisementImageManagementRepository, AdvertisementManagementRepository advertisementManagementRepository) {
        this.advertisementImageManagementRepository = advertisementImageManagementRepository;
        this.advertisementManagementRepository = advertisementManagementRepository;
    }


    public AdvertisementImage addImage(AdvertisementImage advertisementImage) {
        Long advertisementImageId = advertisementImage.getId();
        if (advertisementImage.getAdvertisement() == null)
            throw new AdvertisementNotFoundException("Advertisement can not be null!");
        Long advertisementId = advertisementImage.getAdvertisement().getId();
        if (!advertisementManagementRepository.existsById(advertisementId))
            throw new AdvertisementNotFoundException(String.format("Advertisement with id: %d not exists!", advertisementId));
        if (advertisementImageId == null)
            return advertisementImageManagementRepository.save(advertisementImage);
        try {
            getAdvertisementImageById(advertisementImage.getId());
            LOGGER.error("Failed to add img " + advertisementImage.getAdvertisement().getTitle());
            throw new EntityExistsException(String.format("Advertisement image with id: %d already exists!", advertisementImage.getId()));
        } catch (EntityNotFoundException e) {
            LOGGER.info("Image " + advertisementImage.getAdvertisement().getTitle() + " has been added.");
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
        LOGGER.info("Image " + id + " has been removed.");
    }

    public List<AdvertisementImage> getAllAdvertisementImages(Long advertisementId) {
        return advertisementImageManagementRepository.findByAdvertisement_Id(advertisementId);
    }
}
