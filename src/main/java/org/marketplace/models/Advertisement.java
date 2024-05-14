package org.marketplace.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.marketplace.enums.AdvertisementStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertisementId")
    private Long id;
    @NotNull(message = "Title cannot be null")
    @Size(min = 5, max = 20, message = "Advertisement title must be between 5 and 20 characters long")
    private String title;
    @NotNull(message = "Advertisement requires a description")
    @Size(min = 10, max = 1000, message = "Advertisement description must be between 10 and 1000 characters long")

    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    @NotNull(message = "Category has to be defined for advertisement")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId")
    @NotNull(message = "User has to be defined for advertisement")
    private User user;
    @NotNull(message = "Price has to be defined for advertisement")
    @Min(value = 0, message = "Price cannot be negative")
    private double price;
    private String location;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private AdvertisementStatus status;
    private Long buyerId;

    public Advertisement(Long id,String title, String description, Category category, User user, double price, String location) {
        this.id=id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.user=user;
        this.price = price;
        this.location = location;
        this.status = AdvertisementStatus.ACTIVE;
        this.buyerId = null;
    }
    public Advertisement(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AdvertisementStatus getStatus() {
        return status;
    }

    public void setStatus(AdvertisementStatus status) {
        this.status = status;
    }

}
