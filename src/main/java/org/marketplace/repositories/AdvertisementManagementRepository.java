package org.marketplace.repositories;

import org.marketplace.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdvertisementManagementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {
    List<Advertisement> findByCategory_Id(Long categoryId);
    List<Advertisement> findByUser_Id(Long userId);
}