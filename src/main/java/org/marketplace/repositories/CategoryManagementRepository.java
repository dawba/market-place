package org.marketplace.repositories;

import org.marketplace.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryManagementRepository extends JpaRepository<Category, Long> {
}
