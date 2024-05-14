package org.marketplace.repositories;

import java.util.List;
import org.marketplace.models.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementImageManagementRepository extends JpaRepository<AdvertisementImage, Long> {
    List<AdvertisementImage> findByAdvertisement_Id(Long advertisementId);
}
