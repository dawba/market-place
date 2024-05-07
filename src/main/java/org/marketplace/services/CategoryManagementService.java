package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.repositories.CategoryManagementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryManagementService {
    private final CategoryManagementRepository categoryManagementRepository;

    public CategoryManagementService(CategoryManagementRepository categoryManagementRepository) {
        this.categoryManagementRepository = categoryManagementRepository;
    }

    public Category addCategory(Category category) {
        return categoryManagementRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryManagementRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id: %d was not found", id)));
    }
    public List<Advertisement> getAdvertisements()
    {
       return null;
    }
}
