package org.marketplace.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.marketplace.enums.ResourceType;
import org.marketplace.models.Advertisement;
import org.marketplace.requests.Response;
import org.marketplace.requests.UserNotFoundException;
import org.marketplace.services.AdvertisementManagementService;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     *
     * @param advertisement advertisement to be added
     * @return added advertisement with HTTP status code
     */
    @PostMapping("/add")
    public ResponseEntity<Response> requestAddAdvertisement(@Valid @RequestBody Advertisement advertisement) {
        try {
            Advertisement ad = advertisementManagementService.addAdvertisement(advertisement);
            logger.info("Advertisement added successfully.\n" + ad.toString());
            Response response = new Response<>(ad, "Advertisement added successfully", HttpStatus.CREATED);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFoundException ex) {
            Response response = new Response<>(null, "User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (EntityExistsException ex) {
            Response response = new Response<>(null, "Advertisement already exists: " + ex.getMessage(), HttpStatus.CONFLICT);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (EntityNotFoundException ex) {
            Response<Advertisement> response = new Response<>(null, "Category not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            Response<Advertisement> response = new Response<>(null, "Invalid advertisement data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response<Advertisement> response = new Response<>(null, "An error occurred during advertisement adding: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete an advertisement
     *
     * @param id id of the advertisement to be deleted
     */

    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.Advertisement', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> requestDeleteAdvertisement(@PathVariable Long id) {
        advertisementManagementService.deleteAdvertisement(id);
        logger.info("Advertisement deleted successfully for ID: " + id);
        Response response = new Response<>(id, String.format("Advertisements deleted successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update an advertisement
     *
     * @param advertisement advertisement to be updated
     * @return updated advertisement with HTTP status code
     */
    @PutMapping("/update")
    public ResponseEntity<Response> requestUpdateAdvertisement(@RequestBody Advertisement advertisement) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.ADVERTISEMENT, advertisement.getId());
        Advertisement ad = advertisementManagementService.updateAdvertisement(advertisement);
        logger.info("Advertisement updated successfully.\n " + ad.toString());
        Response response = new Response<>(ad, String.format("Advertisement updated successfully for ID: %d", advertisement.getId()), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get an advertisement by id
     *
     * @param id id of the advertisement to be retrieved
     * @return advertisement with HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> requestGetAdvertisement(@PathVariable Long id) {
        Advertisement ad = advertisementManagementService.getAdvertisementById(id);
        logger.info("Request for advertisement with id: " + id + "\n" + ad.toString());
        Response response = new Response<>(ad, String.format("Advertisement retrieved successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all advertisements
     *
     * @return list of all advertisements with HTTP status code
     */
    @GetMapping("/all")
    public ResponseEntity<Response> requestGetAllAdvertisements() {
        List<Advertisement> ads = advertisementManagementService.getAllAdvertisements();
        logger.info("Retrieved all advertisements.");
        Response response = new Response<>(ads, "Advertisements retrieved successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get advertisements by category
     *
     * @param id id of the category
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<Response> requestGetAdvertisementsByCategory(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByCategory(id);
        logger.info("Request for advertisements for category ID: " + id);
        Response response = new Response<>(ads, String.format("Advertisements retrieved successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get advertisements by user
     *
     * @param id id of the user
     * @return list of advertisements with HTTP status code
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Response> requestGetAdvertisementsByUser(@PathVariable Long id) {
        List<Advertisement> ads = advertisementManagementService.getAdvertisementsByUser(id);
        logger.info("Request for advertisements for user ID: " + id);
        Response response = new Response<>(ads, String.format("Advertisements retrieved successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buy an advertisement
     *
     * @param id id of the advertisement to be bought
     * @return advertisement with HTTP status code
     */
    @PutMapping("/buy/{id}")
    public ResponseEntity<Response> buyAdvertisementWithId(@PathVariable Long id) {
        Long currentUserId = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getId();
        Advertisement ad = advertisementManagementService.buyAdvertisement(id, currentUserId);
        logger.info("Request to buy advertisement with ID: " + id + " by user with ID: " + currentUserId);
        Response response = new Response<>(ad, String.format("Advertisement with id: %d was bought by user with id: %d", id, currentUserId), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Observe an advertisement for tracking changes
     *
     * @param id id of the advertisement to be observed
     * @return advertisement with HTTP status code
     */
    @PutMapping("/observe/{id}")
    public ResponseEntity<Response> observeAdvertisementWithId(@PathVariable Long id) {
        String email = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getEmail();
        Advertisement ad = advertisementManagementService.observeAdvertisement(id, email);
        logger.info("Request to observe advertisement with ID: " + id + " by user with email: " + email);
        Response response = new Response<>(ad, String.format("Advertisement with id: %d was observed by user with id: %s", id, email), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Unobserve an advertisement
     *
     * @param id id of the advertisement to be unobserved
     * @return advertisement with HTTP status code
     */
    @PutMapping("/unobserve/{id}")
    public ResponseEntity<Response> unobserveAdvertisementWithId(@PathVariable Long id) {
        String email = resourceAccessAuthorizationService.extractCurrentUserFromAuth().getEmail();
        Advertisement ad = advertisementManagementService.unobserveAdvertisement(id, email);
        logger.info("Request to unobserve advertisement with ID: " + id + " by user with email: " + email);
        Response response = new Response<>(ad, String.format("Advertisement with id: %d was unobserved by user with id: %s", id, email), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Change the status of an advertisement
     *
     * @param id     id of the advertisement
     * @param status new status of the advertisement
     * @return advertisement with HTTP status code
     */
    @PutMapping("/change-status/{id}/{status}")
    public ResponseEntity<Response> changeAdvertisementStatus(@PathVariable Long id, @PathVariable String status) {
        Advertisement ad = advertisementManagementService.changeAdvertisementStatus(id, status);
        logger.info("Request to change status of advertisement with ID: " + id + " to: " + status);
        Response response = new Response<>(ad, String.format("Advertisement with id: %d status was changed to: %s", id, status), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Response> requestSearchAdvertisements(@RequestParam Map<String, String> searchParams) {
        List<Advertisement> ads = advertisementManagementService.searchAdvertisements(searchParams);
        logger.info("Request for advertisements with search parameters: " + searchParams);
        Response response = new Response<>(ads, "Advertisements retrieved successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
