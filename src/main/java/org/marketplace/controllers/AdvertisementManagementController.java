package org.marketplace.controllers;


import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.services.AdvertisementManagementService;
import org.marketplace.services.CategoryManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advertisement")
public class AdvertisementManagementController {
    private final AdvertisementManagementService advertisementManagementService;
    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService) {
        this.advertisementManagementService = advertisementManagementService;
    }

    @PostMapping("/add")
    public Advertisement requestAddAdvertisement(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.addAdvertisement(advertisement);
    }

    @DeleteMapping("/delete/{id}")
    public void requestDeleteAdvertisement(@PathVariable Long id) {
        advertisementManagementService.deleteAdvertisement(id);
    }

    @PutMapping("/update/{id}")
    public Advertisement requestUpdateAdvertisement(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.updateAdvertisement(advertisement);
    }

    @GetMapping("/get/{id}")
    public Advertisement requestGetAdvertisement(@PathVariable Long id) {
        return advertisementManagementService.getAdvertisementById(id);
    }

    @GetMapping("get/all")
    public List<Advertisement> requestGetAllAdvertisements() {
        return advertisementManagementService.getAllAdvertisements();
    }

    @GetMapping("get/category/{id}")
    public List<Advertisement> requestGetAdvertisementsByCategory(@PathVariable Long id) {
        return advertisementManagementService.getAdvertisementsByCategory(id);
    }

    @GetMapping("get/user/{id}")
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
