package org.marketplace.controllers;

import org.marketplace.models.AdvertisementImage;
import org.marketplace.services.AdvertisementImageManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advertisement-images")
public class AdvertisementImageManagementController {
    private final AdvertisementImageManagementService advertisementImageManagementService;

    public AdvertisementImageManagementController(AdvertisementImageManagementService advertisementImageManagementService) {
        this.advertisementImageManagementService = advertisementImageManagementService;
    }

    @PostMapping("/add")
    public AdvertisementImage requestAddImage(@RequestBody AdvertisementImage advertisementImage) {
        return this.advertisementImageManagementService.addImage(advertisementImage);
    }
}
