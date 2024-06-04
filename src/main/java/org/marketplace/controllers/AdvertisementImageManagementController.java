package org.marketplace.controllers;

import org.marketplace.enums.ResourceType;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.requests.Response;
import org.marketplace.services.AdvertisementImageManagementService;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement-images")
public class AdvertisementImageManagementController {
    private final AdvertisementImageManagementService advertisementImageManagementService;
    private final ResourceAccessAuthorizationService resourceAccessAuthorizationService;
    Logger logger = LoggerFactory.getLogger(AdvertisementImageManagementController.class);

    public AdvertisementImageManagementController(AdvertisementImageManagementService advertisementImageManagementService, ResourceAccessAuthorizationService resourceAccessAuthorizationService) {
        this.advertisementImageManagementService = advertisementImageManagementService;
        this.resourceAccessAuthorizationService = resourceAccessAuthorizationService;
    }

    /**
     * Add a new advertisement image
     * @param advertisementImage advertisement image to be added
     * @return added advertisement image
     */
    @PostMapping("/add")
    public Response<AdvertisementImage> requestAddImage(@RequestBody AdvertisementImage advertisementImage) {
        AdvertisementImage image = this.advertisementImageManagementService.addImage(advertisementImage);
        logger.info(String.format("Advertisement image added successfully for advertisement ID: %d", advertisementImage.getAdvertisement().getId()));
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
        logger.info(String.format("Advertisement image retrieved successfully for ID: %d", id));
        return new Response<>(image, String.format("Advertisement image retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update an advertisement image
     * @param advertisementImage advertisement image to be updated
     * @return updated advertisement image with HTTP status code
     */
    @PutMapping("/update")
    public Response<AdvertisementImage> requestUpdateAdvertisementImage(@RequestBody AdvertisementImage advertisementImage) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.ADVERTISEMENT_IMAGE, advertisementImage.getId());
        AdvertisementImage image = this.advertisementImageManagementService.updateAdvertisementImage(advertisementImage);
        logger.info(String.format("Advertisement image updated successfully for ID: %d", advertisementImage.getId()));
        return new Response<>(image, String.format("Advertisement image updated successfully for ID: %d", advertisementImage.getId()), HttpStatus.OK);
    }

    /**
     * Delete an advertisement image
     * @param id id of the advertisement image to be deleted
     */
    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.AdvertisementImage', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public Response<Long> requestDeleteAdvertisementImage(@PathVariable Long id) {
        this.advertisementImageManagementService.deleteAdvertisementImage(id);
        logger.info(String.format("Advertisement image deleted successfully for ID: %d", id));
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
        logger.info(String.format("Advertisement images retrieved successfully for advertisement ID: %d", id));
        return new Response<>(imageList, String.format("Advertisement images retrieved successfully for advertisement ID: %d", id), HttpStatus.OK);
    }
}
