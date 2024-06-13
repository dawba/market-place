package org.marketplace.controllers;


import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.marketplace.enums.ResourceType;
import org.marketplace.models.User;
import org.marketplace.requests.Response;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.marketplace.services.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
    private final UserManagementService userManagementService;
    private final ResourceAccessAuthorizationService resourceAccessAuthorizationService;

    public UserManagementController(UserManagementService userManagementService, ResourceAccessAuthorizationService resourceAccessAuthorizationService) {
        this.userManagementService = userManagementService;
        this.resourceAccessAuthorizationService = resourceAccessAuthorizationService;
    }

    /**
     * Add a user
     *
     * @param user - added user
     * @return added user with HTTP status code
     */
    @PostMapping("/register")
    public ResponseEntity<Response> addUser(@Valid @RequestBody User user) {
        try {
            User addedUser = userManagementService.registerNewUserAccount(user);
            Response<User> response = new Response<>(addedUser, "Email verification link was sent to your provided email address", HttpStatus.CREATED);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            Response<User> response = new Response<>(null, "User already exists with this data" + e.getMessage(), HttpStatus.CONFLICT);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Get all users
     *
     * @return list of all users with HTTP status code
     */
    @GetMapping("/all")
    public ResponseEntity<Response> getAllUsers() {
        List<User> users = userManagementService.getAllUsers();
        logger.info("All users retrieved successfully");
        Response response = new Response<>(users, "All users retrieved successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get user by ID
     *
     * @param id The ID of the user to retrieve
     * @return Response containing the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserById(@PathVariable Long id) {
        User user = userManagementService.getUserById(id);
        logger.info("User retrieved successfully for ID: " + id + "\n" + user);
        Response response = new Response<>(user, String.format("User retrieved successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update user
     *
     * @param user The user object with updated information
     * @return Response containing the updated user
     */
    @PutMapping("/update")
    public ResponseEntity<Response> updateUser(@RequestBody @Valid User user) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.USER, user.getId());
        User updatedUser = userManagementService.updateUserAccount(user);
        logger.info("User updated successfully: " + user);
        Response response = new Response<>(updatedUser, "User updated successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Delete user by ID
     *
     * @param id The ID of the user to delete
     * @return Response with HTTP status
     */
    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.User', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUserById(@PathVariable Long id) {
        userManagementService.deleteUserById(id);
        logger.info("User deleted successfully for ID: " + id);
        Response response = new Response<>(id, String.format("User deleted successfully for ID: %d", id), HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Confirm user account
     *
     * @param token The token to confirm the user account
     * @return Response containing the confirmed user
     */
    @GetMapping("/confirm-account")
    public ResponseEntity<Response> confirmUserAccount(@RequestParam String token) {
        logger.info("Received request to confirm account with token: " + token);

        try {
            User confirmedUser = userManagementService.confirmUserAccount(token);
            logger.info("User account confirmed successfully for token: " + token);
            Response response = new Response<>(confirmedUser, "User account confirmed successfully", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Error confirming user account for token: " + token + ". Error message: " + e.getMessage());
            Response response = new Response<>(null, "Error confirming user account for token: " + token + ".\nError message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
