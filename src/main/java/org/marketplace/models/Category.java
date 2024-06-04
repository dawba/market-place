package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Long id;
    @Size(min = 2, max = 20, message = "Category name must be between 2 and 20 characters long")
    private String name;
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category() {

    }
    public Category(Long id)
    {
        this.id=id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
