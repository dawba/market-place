package org.marketplace.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;
import org.marketplace.enums.ResourceType;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.requests.AdvertisementNotFoundException;
import org.marketplace.requests.Response;
import org.marketplace.services.AdvertisementImageManagementService;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     *
     * @param advertisementImage advertisement image to be added
     * @return added advertisement image
     */
    @PostMapping("/add")
    public ResponseEntity<Response> requestAddImage(@RequestBody AdvertisementImage advertisementImage) {
        try {
            AdvertisementImage image = this.advertisementImageManagementService.addImage(advertisementImage);
            logger.info(String.format("Advertisement image added successfully for advertisement ID: %d", advertisementImage.getAdvertisement().getId()));
            Response response = new Response<>(image, String.format("Advertisement image added successfully for advertisement ID: %d", advertisementImage.getAdvertisement().getId()), HttpStatus.CREATED);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AdvertisementNotFoundException ex) {
            Response response = new Response<>(null, "Advertisement not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (EntityExistsException ex) {
            Response response = new Response<>(null, "Advertisement image already exists: " + ex.getMessage(), HttpStatus.CONFLICT);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (ValidationException e) {
            Response<AdvertisementImage> response = new Response<>(null, "Invalid advertisement-image data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response<AdvertisementImage> response = new Response<>(null, "An error occurred during advertisement-image adding: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get an advertisement image by id
     *
     * @param id id of the advertisement image to be retrieved
     * @return advertisement image with HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> requestGetAdvertisementImageById(@PathVariable Long id) {
        AdvertisementImage image = this.advertisementImageManagementService.getAdvertisementImageById(id);
        logger.info(String.format("Advertisement image retrieved successfully for ID: %d", id));
        Response response = new Response<>(image, String.format("Advertisement image retrieved successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update an advertisement image
     *
     * @param advertisementImage advertisement image to be updated
     * @return updated advertisement image with HTTP status code
     */
    @PutMapping("/update")
    public ResponseEntity<Response> requestUpdateAdvertisementImage(@RequestBody AdvertisementImage advertisementImage) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.ADVERTISEMENT_IMAGE, advertisementImage.getId());
        AdvertisementImage image = this.advertisementImageManagementService.updateAdvertisementImage(advertisementImage);
        logger.info(String.format("Advertisement image updated successfully for ID: %d", advertisementImage.getId()));
        Response response = new Response<>(image, String.format("Advertisement image updated successfully for ID: %d", advertisementImage.getId()), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete an advertisement image
     *
     * @param id id of the advertisement image to be deleted
     */
    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.AdvertisementImage', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> requestDeleteAdvertisementImage(@PathVariable Long id) {
        this.advertisementImageManagementService.deleteAdvertisementImage(id);
        logger.info(String.format("Advertisement image deleted successfully for ID: %d", id));
        Response response = new Response<>(id, String.format("Advertisement image deleted successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all advertisement images for an advertisement
     *
     * @param id id of the advertisement
     * @return list of advertisement images with HTTP status code
     */
    @GetMapping("/advertisement/{id}")
    public ResponseEntity<Response> requestGetAllAdvertisementImages(@PathVariable Long id) {
        List<AdvertisementImage> imageList = this.advertisementImageManagementService.getAllAdvertisementImages(id);
        logger.info(String.format("Advertisement images retrieved successfully for advertisement ID: %d", id));
        Response response = new Response<>(imageList, String.format("Advertisement images retrieved successfully for advertisement ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
