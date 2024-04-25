package org.marketplace.controllers;

import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.services.CategoryManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryManagementController {
    private final CategoryManagementService categoryManagementService;

    public CategoryManagementController(CategoryManagementService categoryManagementService) {
        this.categoryManagementService = categoryManagementService;
    }

    @PostMapping("/add")
    public Category requestAddCategory(@RequestBody Category category) {
        return categoryManagementService.addCategory(category);
    }

}
