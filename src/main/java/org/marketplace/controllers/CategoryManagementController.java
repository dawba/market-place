package org.marketplace.controllers;

import jakarta.validation.Valid;
import org.marketplace.models.Category;
import org.marketplace.requests.Response;
import org.marketplace.services.CategoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryManagementController {
    private final CategoryManagementService categoryManagementService;
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementManagementController.class);


    public CategoryManagementController(CategoryManagementService categoryManagementService) {
        this.categoryManagementService = categoryManagementService;
    }

    /**
     * Add a category
     * @param category - added category
     * @return added category with HTTP status code
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Response<Category> requestAddCategory(@Valid @RequestBody Category category) {
        Category addedCategory = categoryManagementService.addCategory(category);
        logger.info("Category added successfully: " + category);
        return new Response<>(addedCategory, "Category added successfully", HttpStatus.CREATED);
    }

    /**
     * Get all categories
     * @return list of all categories with HTTP status code
     */
    @GetMapping("/all")
    public Response<List<Category>> requestGetAllCategories() {
        List<Category> categories = categoryManagementService.getAllCategories();
        logger.info("Categories retrieved successfully");
        return new Response<>(categories, "All categories retrieved successfully", HttpStatus.OK);
    }

    /**
     * Get a category by id
     * @param id id of the category to be retrieved
     * @return category with HTTP status code
     */
    @GetMapping("/{id}")
    public Response<Category> requestGetCategoryById(@PathVariable Long id) {
        Category category = categoryManagementService.getCategoryById(id);
        logger.info("Category retrieved successfully for ID: " + id + "\n" + category);
        return new Response<>(category, String.format("Category retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update category by id
     * @param category category to be updated
     * @return category updated category
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public Response<Category> requestUpdateCategory(@Valid @RequestBody Category category) {
        Category updatedCategory = categoryManagementService.addCategory(category);
        logger.info("Category updated successfully: " + category);
        return new Response<>(updatedCategory, "Category updated successfully", HttpStatus.OK);
    }

    /**
     * Delete a category by id
     * @param id id of the category to be deleted
     * @return HTTP status code
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Response<Long> requestDeleteCategory(@PathVariable Long id) {
        categoryManagementService.deleteCategory(id);
        logger.info("Category deleted successfully for ID: " + id);
        return new Response<>(id, String.format("Category deleted successfully for ID: %d", id), HttpStatus.OK);
    }
}
