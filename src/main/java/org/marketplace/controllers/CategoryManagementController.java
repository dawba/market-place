package org.marketplace.controllers;

import org.marketplace.models.Category;
import org.marketplace.services.CategoryManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryManagementController {
    private final CategoryManagementService categoryManagementService;

    public CategoryManagementController(CategoryManagementService categoryManagementService){
        this.categoryManagementService = categoryManagementService;
    }

    @PostMapping
    public Category requestAddCategory(@RequestBody Category category){
        return this.categoryManagementService.addCategory(category);
    }
}
