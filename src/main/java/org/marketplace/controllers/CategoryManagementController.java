package org.marketplace.controllers;

import jakarta.validation.Valid;
import org.marketplace.models.Category;
import org.marketplace.requests.Response;
import org.marketplace.services.CategoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/add")
    public Response<Category> requestAddCategory(@Valid @RequestBody Category category) {
        Category addedCategory = categoryManagementService.addCategory(category);
        return new Response<Category>(addedCategory, "Category added successfully", HttpStatus.CREATED);
    }

    /**
     * Get all categories
     * @return list of all categories with HTTP status code
     */
    @GetMapping("/all")
    public Response<List<Category>> requestGetAllCategories() {
        List<Category> categories = categoryManagementService.getAllCategories();
        logger.info("categories length: " + categories.size());
        return new Response<List<Category>>(categories, "All categories retrieved successfully", HttpStatus.OK);
    }

    /**
     * Get a category by id
     * @param id id of the category to be retrieved
     * @return category with HTTP status code
     */
    @GetMapping("/{id}")
    public Response<Category> requestGetCategoryById(@PathVariable Long id) {
        logger.info("Category ID: " + id);
        Category category = categoryManagementService.getCategoryById(id);
        return new Response<Category>(category, String.format("Category retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update category by id
     * @param category category to be updated
     * @return category updated category
     */
    @PutMapping("/update")
    public Response<Category> requestUpdateCategory(@Valid @RequestBody Category category) {
        Category updatedCategory = categoryManagementService.addCategory(category);
        return new Response<Category>(updatedCategory, "Category updated successfully", HttpStatus.OK);
    }

    /**
     * Delete a category by id
     * @param id id of the category to be deleted
     * @return HTTP status code
     */
    @DeleteMapping("/{id}")
    public Response<Long> requestDeleteCategory(@PathVariable Long id) {
        categoryManagementService.deleteCategory(id);
        return new Response<Long>(id, String.format("Category deleted successfully for ID: %d", id), HttpStatus.OK);
    }
}
