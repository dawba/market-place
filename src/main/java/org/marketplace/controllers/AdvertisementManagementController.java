package org.marketplace.controllers;

import jakarta.validation.Valid;
import org.marketplace.enums.ResourceType;
import org.marketplace.requests.Response;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.marketplace.models.Advertisement;
import org.marketplace.services.AdvertisementManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementManagementController {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementManagementController.class);
    private final AdvertisementManagementService advertisementManagementService;

    private final ResourceAccessAuthorizationService resourceAccessAuthorizationService;



    public AdvertisementManagementController(AdvertisementManagementService advertisementManagementService, ResourceAccessAuthorizationService resourceAccessAuthorizationService) {
        this.advertisementManagementService = advertisementManagementService;
        this.resourceAccessAuthorizationService = resourceAccessAuthorizationService;
    }

    /**
     * Add a new advertisement
     * @param advertisement advertisement to be added
     * @return added advertisement with HTTP status code
     */
    @PostMapping("/add")
    public Response<Advertisement> requestAddAdvertisement(@Valid @RequestBody Advertisement advertisement) {
        Advertisement ad = advertisementManagementService.addAdvertisement(advertisement);
        logger.info("Advertisement added successfully.\n" + ad.toString());
        return new Response<>(ad, "Advertisement added successfully", HttpStatus.CREATED);
    }

    /**
     * Delete an advertisement
     * @param id id of the advertisement to be deleted
     */

    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.Advertisement', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public Response<Long> requestDeleteAdvertisement(@PathVariable Long id) {
        advertisementManagementService.deleteAdvertisement(id);
        logger.info("Advertisement deleted successfully for ID: " + id);
        return new Response<>(id, String.format("Advertisements deleted successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update an advertisement
     * @param advertisement advertisement to be updated
     * @return updated advertisement with HTTP status code
     */
    @PutMapping("/update")
    public Response<Advertisement> requestUpdateAdvertisement(@RequestBody Advertisement advertisement) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.ADVERTISEMENT, advertisement.getId());
        Advertisement ad = advertisementManagementService.updateAdvertisement(advertisement);
        logger.info("Advertisement updated successfully.\n " + ad.toString());
        return new Response<>(ad, String.format("Advertisement updated successfully for ID: %d", advertisement.getId()), HttpStatus.OK);
    }

    /**
     * Get an advertisement by id
     * @param id id of the advertisement to be retrieved
     * @return advertisement with HTTP status code
     */
    @GetMapping("/{id}")
    public Response<Advertisement> requestGetAdvertisement(@PathVariable Long id) {
        Advertisement ad = advertisementManagementService.getAdvertisementById(id);
        logger.info("Request for advertisement with id: " + id + "\n" + ad.toString());
        return new Response<>(ad, String.format("Advertisement retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Get all advertisements
     * @return list of all advertisements with HTTP status code
     */
    @GetMapping("/all")
    public Response<List<Advertisement>> requestGetAllAdvertisements() {
        List<Advertisement> ads = advertisementManagementService.getAllAdvertisements();
        logger.info("Retrieved all advertisements.");
        return new Response<>(ads, "Advertisements retrieved successfully", HttpStatus.OK);
    }

    /**
     * Get advertisements by category
     * @param id id of the category
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/category/{id}")
    public Response<List<Advertisement>> requestGetAdvertisementsByCategory(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByCategory(id);
        logger.info("Request for advertisements for category ID: " + id);
        return new Response<>(ads, String.format("Advertisements retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Get advertisements by user
     * @param id id of the user
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/user/{id}")
    public Response<List<Advertisement>> requestGetAdvertisementsByUser(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByUser(id);
        logger.info("Request for advertisements for user ID: " + id);
        return new Response<>(ads, String.format("Advertisements retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Buy an advertisement
     * @param id id of the advertisement to be bought
     * @return advertisement with HTTP status code
     */
    @PutMapping("/buy/{id}")
    public Response<Advertisement> buyAdvertisementWithId(@PathVariable Long id) {
        Long currentUserId = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getId();
        Advertisement ad = advertisementManagementService.buyAdvertisement(id, currentUserId);
        logger.info("Request to buy advertisement with ID: " + id + " by user with ID: " + currentUserId);
        return new Response<>(ad, String.format("Advertisement with id: %d was bought by user with id: %d", id, currentUserId), HttpStatus.OK);
    }

    /**
     * Observe an advertisement for tracking changes
     * @param id id of the advertisement to be observed
     * @return advertisement with HTTP status code
     */
    @PutMapping("/observe/{id}")
    public Response<Advertisement> observeAdvertisementWithId(@PathVariable Long id) {
        String email = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getEmail();
        Advertisement ad = advertisementManagementService.observeAdvertisement(id, email);
        logger.info("Request to observe advertisement with ID: " + id + " by user with email: " + email);
        return new Response<>(ad, String.format("Advertisement with id: %d was observed by user with id: %s", id, email), HttpStatus.OK);
    }

    /**
     * Unobserve an advertisement
     * @param id id of the advertisement to be unobserved
     * @return advertisement with HTTP status code
     */
    @PutMapping("/unobserve/{id}")
    public Response<Advertisement> unobserveAdvertisementWithId(@PathVariable Long id) {
        String email = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getEmail();
        Advertisement ad = advertisementManagementService.unobserveAdvertisement(id, email);
        logger.info("Request to unobserve advertisement with ID: " + id + " by user with email: " + email);
        return new Response<>(ad, String.format("Advertisement with id: %d was unobserved by user with id: %s", id, email), HttpStatus.OK);
    }

    /**
     * Change the status of an advertisement
     * @param id id of the advertisement
     * @param status new status of the advertisement
     * @return advertisement with HTTP status code
     */
    @PutMapping ("/change-status/{id}/{status}")
    public Response<Advertisement> changeAdvertisementStatus(@PathVariable Long id, @PathVariable String status) {
        Advertisement ad = advertisementManagementService.changeAdvertisementStatus(id, status);
        logger.info("Request to change status of advertisement with ID: " + id + " to: " + status);
        return new Response<>(ad, String.format("Advertisement with id: %d status was changed to: %s", id, status), HttpStatus.OK);
    }

    @GetMapping("/search")
    public Response<List<Advertisement>> requestSearchAdvertisements(@RequestParam Map<String, String> searchParams) {
        List<Advertisement> ads = advertisementManagementService.searchAdvertisements(searchParams);
        logger.info("Request for advertisements with search parameters: " + searchParams);
        return new Response<>(ads, "Advertisements retrieved successfully", HttpStatus.OK);
    }
}
