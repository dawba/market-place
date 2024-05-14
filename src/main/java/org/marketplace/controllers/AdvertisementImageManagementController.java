package org.marketplace.controllers;

import org.marketplace.models.AdvertisementImage;
import org.marketplace.requests.Response;
import org.marketplace.services.AdvertisementImageManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement-images")
public class AdvertisementImageManagementController {
    private final AdvertisementImageManagementService advertisementImageManagementService;

    public AdvertisementImageManagementController(AdvertisementImageManagementService advertisementImageManagementService) {
        this.advertisementImageManagementService = advertisementImageManagementService;
    }

    /**
     * Add a new advertisement image
     * @param advertisementImage advertisement image to be added
     * @return added advertisement image
     */
    @PostMapping("/add")
    public Response<AdvertisementImage> requestAddImage(@RequestBody AdvertisementImage advertisementImage) {
        AdvertisementImage image = this.advertisementImageManagementService.addImage(advertisementImage);
        return new Response<>(image, String.format("Advertisement image added successfully for advertisement ID: %d", advertisementImage.getAdvertisement().getId()), HttpStatus.CREATED);
    }

    /**
     * Get an advertisement image by id
     * @param id id of the advertisement image to be retrieved
     * @return advertisement image with HTTP status code
     */
    @GetMapping("/{id}")
    public Response<AdvertisementImage> requestGetAdvertisementImageById(@PathVariable Long id) {
        AdvertisementImage image = this.advertisementImageManagementService.getAdvertisementImageById(id);
        return new Response<>(image, String.format("Advertisement image retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update an advertisement image
     * @param advertisementImage advertisement image to be updated
     * @return updated advertisement image with HTTP status code
     */
    @PutMapping("/update")
    public Response<AdvertisementImage> requestUpdateAdvertisementImage(@RequestBody AdvertisementImage advertisementImage) {
        AdvertisementImage image = this.advertisementImageManagementService.updateAdvertisementImage(advertisementImage);
        return new Response<>(image, String.format("Advertisement image updated successfully for ID: %d", advertisementImage.getId()), HttpStatus.OK);
    }

    /**
     * Delete an advertisement image
     * @param id id of the advertisement image to be deleted
     */
    @DeleteMapping("/{id}")
    public Response<Long> requestDeleteAdvertisementImage(@PathVariable Long id) {
        this.advertisementImageManagementService.deleteAdvertisementImage(id);
        return new Response<>(id, String.format("Advertisement image deleted successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Get all advertisement images for an advertisement
     * @param id id of the advertisement
     * @return list of advertisement images with HTTP status code
     */
    @GetMapping("/advertisement/{id}")
    public Response<List<AdvertisementImage>> requestGetAllAdvertisementImages(@PathVariable Long id) {
        List<AdvertisementImage> imageList = this.advertisementImageManagementService.getAllAdvertisementImages(id);
        return new Response<>(imageList, String.format("Advertisement images retrieved successfully for advertisement ID: %d", id), HttpStatus.OK);
    }
}
