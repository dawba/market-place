package org.marketplace.repositories;

import org.marketplace.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementManagementRepository extends JpaRepository<Advertisement, Long> {
}
