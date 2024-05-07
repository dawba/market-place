package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.Advertisement;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementManagementService {
    private final AdvertisementManagementRepository advertisementManagementRepository;

    public AdvertisementManagementService(AdvertisementManagementRepository advertisementManagementRepository) {
        this.advertisementManagementRepository = advertisementManagementRepository;
    }

    public Advertisement addAdvertisement(Advertisement advertisement) {
        return advertisementManagementRepository.save(advertisement);
    }

    public List<Advertisement> getAllAdvertisements() {
        return advertisementManagementRepository.findAll();
    }

    public Advertisement getAdvertisementById(Long id) {
        return advertisementManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement with id: %d was not found", id)));
    }
/*
    public List<Advertisement> getAdvertisementsByCategoryId(Long id) {
        var advertisements = advertisementManagementRepository.findByCategoryId(id);
        if (advertisements == null)
            throw new EntityNotFoundException(String.format("Category with id: %d was not found", id));
        return advertisements;
    }
*/

}
