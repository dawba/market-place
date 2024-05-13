package org.marketplace.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.services.AdvertisementManagementService;
import org.marketplace.services.CategoryManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementManagementController {

    private final AdvertisementManagementService advertisementManagementService;
    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService) {
        this.advertisementManagementService = advertisementManagementService;
    }

    @PostMapping("/add")
    public Advertisement requestAddAdvertisement(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.addAdvertisement(advertisement);
    }

    @DeleteMapping("/{id}")
    public void requestDeleteAdvertisement(@PathVariable Long id) {
        advertisementManagementService.deleteAdvertisement(id);
    }

    @PutMapping("/{id}")
    public Advertisement requestUpdateAdvertisement(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.updateAdvertisement(advertisement);
    }

    @GetMapping("/{id}")
    public Advertisement requestGetAdvertisement(@PathVariable Long id) {
        return advertisementManagementService.getAdvertisementById(id);
    }

    @GetMapping("/all")
    public List<Advertisement> requestGetAllAdvertisements() {
        return advertisementManagementService.getAllAdvertisements();
    }

    @GetMapping("/category/{id}")
    public List<Advertisement> requestGetAdvertisementsByCategory(@PathVariable Long id) {
        return advertisementManagementService.getAdvertisementsByCategory(id);
    }

    @GetMapping("/user/{id}")
    public List<Advertisement> requestGetAdvertisementsByUser(@PathVariable Long id) {
        return advertisementManagementService.getAdvertisementsByUser(id);
    }

    /*

    additional methods that can be implemented in the future:

    - deleting all user ads
    - deleting all category ads
    - getting a sum of categories

     */
}
