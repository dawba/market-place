package org.marketplace.repositories;

import org.marketplace.models.Advertisement;
import org.marketplace.models.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementImageManagementRepository extends JpaRepository<AdvertisementImage, Long> {
    Advertisement findByAdvertisementId(Long advertisementId);
}
