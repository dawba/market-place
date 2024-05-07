package org.marketplace.models;

import jakarta.persistence.*;

@Entity
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertisementImageId")
    private Long id;

    private String filename;
    @ManyToOne
    @JoinColumn(name = "advertisementId")
    private Advertisement advertisement;

    public AdvertisementImage(Long id, String filename, Advertisement advertisement) {
        this.id = id;
        this.filename = filename;
        this.advertisement=advertisement;
    }

    public AdvertisementImage() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

}
