package org.marketplace.repositories;

import org.marketplace.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementManagementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByCategoryId(Long categoryId);
}