package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Filepath cannot be null")
    @Column(name = "filepath")
    private String filepath;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Advertisement advertisement;

    public AdvertisementImage(Long id, String filepath, Advertisement advertisement) {
        this.id = id;
        this.filepath = filepath;
        this.advertisement = advertisement;
    }

    public AdvertisementImage() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
