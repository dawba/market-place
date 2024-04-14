package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.List;

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Size(min = 2,max = 20, message = "Category name must be between 2 and 20 characters long")
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy ="category", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Advertisement> advertisements;
}
