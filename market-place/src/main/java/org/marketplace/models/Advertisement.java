package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long advertisementId;
    @Size(min = 5,max = 20, message = "Advertisement title must be between 5 and 20 characters long")
    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advertisement", cascade = CascadeType.ALL)
    private List<AdvertisementImage> images;
    private double price;
    private String location;

    private Clock createdAt;
    private Clock updatedAt;
    @PrePersist
    protected void onCreate(){
        //createdAt =
        //updatedAt =
    }

    @PreUpdate
    protected void onUpdate(){
        //updatedAt =
    }

    private Long status; //if status>0 it is id of buyer, else if status <=0: 0-ACTIVE, -1-INACTIVE, -2-DELETED, -3-EDITED

}
