package org.marketplace.models;

import jakarta.persistence.*;

@Entity
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename; // lub ścieżka do pliku

    @ManyToOne
    @JoinColumn(name = "advertisementId")
    private Advertisement advertisement;
}
