package org.marketplace.controllers;


import jakarta.validation.Valid;
import org.marketplace.enums.ResourceType;
import org.marketplace.models.User;
import org.marketplace.requests.Response;
import org.marketplace.services.ResourceAccessAuthorizationService;
import org.marketplace.services.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
     * @param user - added user
     * @return added user with HTTP status code
     */
    @PostMapping("/register")
    public Response<User> addUser(@RequestBody @Valid User user) {
        User addedUser = userManagementService.registerNewUserAccount(user);
        return new Response<>(addedUser, "User registered successfully", HttpStatus.CREATED);
    }

    /**
     * Get all users
     * @return list of all users with HTTP status code
     */
    @GetMapping("/all")
    public Response<List<User>> getAllUsers()
    {
        List<User> users = userManagementService.getAllUsers();
        return new Response<>(users, "All users retrieved successfully", HttpStatus.OK);
    }

    /**
     * Get user by ID
     * @param id The ID of the user to retrieve
     * @return Response containing the user
     */
    @GetMapping("/{id}")
    public Response<User> getUserById(@PathVariable Long id) {
        logger.info("User ID: " + id);
        User user = userManagementService.getUserById(id);
        return new Response<>(user, String.format("User retrieved successfully for ID: %d", id), HttpStatus.OK);
    }

    /**
     * Update user
     * @param user The user object with updated information
     * @return Response containing the updated user
     */
    @PutMapping("/update")
    public Response<User> updateUser(@RequestBody @Valid User user, @RequestHeader("Authorization") String token) {
        resourceAccessAuthorizationService.authorizeUserAccessFromRequestBodyOrThrow(ResourceType.USER, user.getId());

        User updatedUser = userManagementService.updateUser(user, token);
        return new Response<>(updatedUser, "User updated successfully", HttpStatus.OK);

    }


    /**
     * Delete user by ID
     * @param id The ID of the user to delete
     * @return Response with HTTP status
     */
    @PreAuthorize("@resourceAccessAuthorizationService.authorizeUserAccess('ResourceType.User', #id).equals(T(org.marketplace.enums.AccessStatus).ACCESS_GRANTED)")
    @DeleteMapping("/{id}")
    public Response<Long> deleteUserById(@PathVariable Long id) {
        userManagementService.deleteUserById(id);
        return new Response<>(id, String.format("User deleted successfully for ID: %d", id), HttpStatus.OK);
    }

}
