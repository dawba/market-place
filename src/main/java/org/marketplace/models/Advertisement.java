package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long advertisementId;
    @Size(min = 5, max = 20, message = "Advertisement title must be between 5 and 20 characters long")
    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advertisement", cascade = CascadeType.ALL)
    private List<AdvertisementImage> images;
    private double price;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int status; //if status>0 it is id of buyer, else if status <=0: 0-ACTIVE, -1-INACTIVE, -2-DELETED, -3-EDITED

    public Advertisement(Long advertisementId, String title, String description, Category category, List<AdvertisementImage> images, double price, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.advertisementId = advertisementId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.images = images;
        this.price = price;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = 0;
    }

    public Advertisement() {

    }

    public Long getAdvertisementId() {
        return advertisementId;
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

    public List<AdvertisementImage> getImages() {
        return images;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
