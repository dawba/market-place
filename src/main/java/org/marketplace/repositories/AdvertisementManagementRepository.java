package org.marketplace.repositories;

import org.marketplace.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface AdvertisementManagementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByCategory_Id(Long categoryId);
    List<Advertisement> findByUser_Id(Long userId);
}