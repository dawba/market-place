package org.marketplace.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.marketplace.models.Advertisement;
import org.marketplace.services.AdvertisementManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementManagementController {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementManagementController.class);
    private final AdvertisementManagementService advertisementManagementService;
    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService) {
        this.advertisementManagementService = advertisementManagementService;
    }

    /**
     * Add a new advertisement
     * @param advertisement advertisement to be added
     * @return added advertisement with HTTP status code
     */
    @PostMapping("/add")
    public ResponseEntity<Advertisement> requestAddAdvertisement(@Valid @RequestBody Advertisement advertisement) {
        Advertisement ad = advertisementManagementService.addAdvertisement(advertisement);
        return ResponseEntity.ok(ad);
    }

    /**
     * Delete an advertisement
     * @param id id of the advertisement to be deleted
     */

    @DeleteMapping("/{id}")
    public void requestDeleteAdvertisement(@PathVariable Long id) {
        advertisementManagementService.deleteAdvertisement(id);
    }

    /**
     * Update an advertisement
     * @param advertisement advertisement to be updated
     * @return updated advertisement with HTTP status code
     */
    @PutMapping("/{id}")
    public Advertisement requestUpdateAdvertisement(@RequestBody Advertisement advertisement) {
        return advertisementManagementService.updateAdvertisement(advertisement);
    }

    /**
     * Get an advertisement by id
     * @param id id of the advertisement to be retrieved
     * @return advertisement with HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> requestGetAdvertisement(@PathVariable Long id) {
        Advertisement ad = advertisementManagementService.getAdvertisementById(id);
        return ResponseEntity.ok(ad);
    }

    /**
     * Get all advertisements
     * @return list of all advertisements with HTTP status code
     */
    @GetMapping("/all")
    public ResponseEntity<List<Advertisement>> requestGetAllAdvertisements() {
        List<Advertisement> ads = advertisementManagementService.getAllAdvertisements();
        return ResponseEntity.ok(ads);
    }

    /**
     * Get advertisements by category
     * @param id id of the category
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<List<Advertisement>> requestGetAdvertisementsByCategory(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByCategory(id);
        return ResponseEntity.ok(ads);
    }

    /**
     * Get advertisements by user
     * @param id id of the user
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Advertisement>> requestGetAdvertisementsByUser(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByUser(id);
        return ResponseEntity.ok(ads);
    }
}
