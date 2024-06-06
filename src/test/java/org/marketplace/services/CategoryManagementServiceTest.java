package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.models.Category;
import org.marketplace.repositories.CategoryManagementRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryManagementServiceTest {

    @Mock
    private CategoryManagementRepository categoryManagementRepository;

    @InjectMocks
    private CategoryManagementService categoryManagementService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "newCategory");
    }

    @Test
    void testAddCategory() {
        given(categoryManagementRepository.save(category)).willReturn(category);

        Category addedCategory = categoryManagementService.addCategory(category);

        assertThat(addedCategory).isNotNull();
        assertThat(addedCategory.getId()).isEqualTo(1L);
        assertThat(addedCategory.getName()).isEqualTo("newCategory");
    }

    @Test
    public void testAddCategoryWithNullName() {
        Category invalidCategory = new Category();
        invalidCategory.setName(null);

        given(categoryManagementRepository.save(invalidCategory)).willThrow(new ConstraintViolationException("Category name cannot be null", null));

        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, () -> {
            categoryManagementService.addCategory(invalidCategory);
        });

        assertThat(thrown.getMessage()).isEqualTo("Category name cannot be null");
    }

    @Test
    public void testAddCategoryWithShortName() {
        // given
        Category invalidCategory = new Category();
        invalidCategory.setName("A");
        given(categoryManagementRepository.save(invalidCategory)).willThrow(new ConstraintViolationException("Category name must be between 2 and 20 characters long", null));

        // when
        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, () -> {
            categoryManagementService.addCategory(invalidCategory);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Category name must be between 2 and 20 characters long");
    }

    @Test
    public void testGetAllCategories() {
        Category category1 = new Category(2L, "secondCategory");

        given(categoryManagementRepository.findAll()).willReturn(List.of(category, category1));

        List<Category> categoryList = categoryManagementService.getAllCategories();

        assertThat(categoryList).isNotNull();
        assertThat(categoryList.size()).isGreaterThan(1);
        assertThat(categoryList).contains(category,category1);
    }

    @Test
    public void testGetCategoryById() {
        given(categoryManagementRepository.findById(1L)).willReturn(Optional.of(category));

        Category foundCategory = categoryManagementService.getCategoryById(1L);

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(1L);
        assertThat(foundCategory.getName()).isEqualTo("newCategory");
    }

    @Test
    public void testGetCategoryByIdNotFound() {
        given(categoryManagementRepository.findById(1L)).willReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            categoryManagementService.getCategoryById(1L);
        });

        assertThat(thrown.getMessage()).isEqualTo("Category with id: 1 was not found");
    }

    @Test
    public void testUpdateCategory() {
        given(categoryManagementRepository.findById(1L)).willReturn(Optional.of(category));
        category.setName("updatedCategory");
        given(categoryManagementRepository.save(category)).willReturn(category);

        Category updatedCategory = categoryManagementService.updateCategory(category);

        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getId()).isEqualTo(1L);
        assertThat(updatedCategory.getName()).isEqualTo("updatedCategory");
    }

    @Test
    public void testUpdateCategoryNotFound() {
        given(categoryManagementRepository.findById(1L)).willReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            categoryManagementService.updateCategory(category);
        });

        assertThat(thrown.getMessage()).isEqualTo("Category with id: 1 was not found");
    }

    @Test
    public void testDeleteCategory() {
        // precondition
        given(categoryManagementRepository.existsById(1L)).willReturn(true);

        // action
        categoryManagementService.deleteCategory(category.getId());

        // verify
        verify(categoryManagementRepository, times(1)).deleteById(category.getId());
    }

    @Test
    public void testDeleteCategoryNotFound() {
        given(categoryManagementRepository.existsById(1L)).willReturn(false);

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            categoryManagementService.deleteCategory(1L);
        });

        assertThat(thrown.getMessage()).isEqualTo("Category with id: 1 was not found");

    }
}