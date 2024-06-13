package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marketplace.models.Category;
import org.marketplace.repositories.CategoryManagementRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryManagementServiceTest {

    @Mock
    private CategoryManagementRepository categoryManagementRepository;

    @InjectMocks
    private CategoryManagementService categoryManagementService;

    private Category category;

    private static Stream<Arguments> provideInvalidCategoryNames() {
        return Stream.of(
                Arguments.of(null, "Category name cannot be null"),
                Arguments.of("A", "Category name must be between 2 and 20 characters long"),
                Arguments.of("A".repeat(21), "Category name must be between 2 and 20 characters long")
        );
    }

    private static Stream<Arguments> provideValidCategoryNames() {
        return Stream.of(
                Arguments.of("Aa"),
                Arguments.of("A".repeat(20))
        );
    }

    @BeforeEach
    void setUp() {
        category = new Category(1L, "newCategory");
    }

    @ParameterizedTest(name = "{index} => name={0}, expectedMessage={1}")
    @MethodSource("provideInvalidCategoryNames")
    public void testAddCategoryWithInvalidNames(String name, String expectedMessage) {
        Category category = new Category();
        category.setName(name);

        given(categoryManagementRepository.save(category))
                .willThrow(new ConstraintViolationException(expectedMessage, null));

        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, () -> {
            categoryManagementService.addCategory(category);
        });

        assertThat(thrown.getMessage()).isEqualTo(expectedMessage);
    }

    @ParameterizedTest(name = "{index} => name={0}}")
    @MethodSource("provideValidCategoryNames")
    void testAddCategoryWithValidNames(String name) {
        Category category = new Category();
        category.setName(name);
        given(categoryManagementRepository.save(category)).willReturn(category);

        Category addedCategory = categoryManagementService.addCategory(category);

        assertThat(addedCategory).isNotNull();
        assertThat(addedCategory.getName()).isEqualTo(name);
    }

    @Test
    public void testGetAllCategories() {
        Category category1 = new Category(2L, "secondCategory");

        given(categoryManagementRepository.findAll()).willReturn(List.of(category, category1));

        List<Category> categoryList = categoryManagementService.getAllCategories();

        assertThat(categoryList).isNotNull();
        assertThat(categoryList.size()).isGreaterThan(1);
        assertThat(categoryList).contains(category, category1);
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