package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.Advertisement;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementImageManagementService {
    private final AdvertisementImageManagementRepository advertisementImageManagementRepository;

    public AdvertisementImageManagementService(AdvertisementImageManagementRepository advertisementImageManagementRepository) {
        this.advertisementImageManagementRepository = advertisementImageManagementRepository;
    }

    public AdvertisementImage addImage(AdvertisementImage advertisementImage) {
        return advertisementImageManagementRepository.save(advertisementImage);
    }

    public AdvertisementImage getAdvertisementImageById(Long id) {
        return advertisementImageManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", id)));
    }

  /*  public Advertisement getAdvertisementImageByAdvertisementId(Long id) {
        Advertisement advertisement = advertisementImageManagementRepository.findByAdvertisementId(id);
        if (advertisement == null)
            throw new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id));
        return advertisement;

    }

   */
}
