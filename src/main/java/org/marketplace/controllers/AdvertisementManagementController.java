package org.marketplace.controllers;


import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.services.AdvertisementManagementService;
import org.marketplace.services.CategoryManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advertisement")
public class AdvertisementManagementController {
    private final AdvertisementManagementService advertisementManagementService;
    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService) {
        this.advertisementManagementService = advertisementManagementService;
    }

    @PostMapping("/add")
    public Advertisement requestAddOffer(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.addAdvertisement(advertisement);
    }
}
