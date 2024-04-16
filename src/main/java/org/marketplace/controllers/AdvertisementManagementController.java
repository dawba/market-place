package org.marketplace.controllers;


import org.marketplace.models.Advertisement;
import org.marketplace.services.AdvertisementManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advertisements")
public class AdvertisementManagementController {
    private final AdvertisementManagementService advertisementManagementService;

    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService){
        this.advertisementManagementService = advertisementManagementService;
    }

    @PostMapping
    public Advertisement requestAddOffer(@RequestBody Advertisement advertisement){
        return advertisementManagementService.addAdvertisement(advertisement);
    }
}