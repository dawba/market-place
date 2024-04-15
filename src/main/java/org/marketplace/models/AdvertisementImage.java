package org.marketplace.models;

import jakarta.persistence.*;

@Entity
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String filename;
    @ManyToOne
    @JoinColumn(name = "advertisementId")
    private final Advertisement advertisement;

    public AdvertisementImage(Long id, String filename, Advertisement advertisement) {
        this.id = id;
        this.filename = filename;
        this.advertisement = advertisement;
    }

    public String getFilename() {
        return filename;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }
}
