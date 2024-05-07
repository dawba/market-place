package org.marketplace.controllers;

import org.marketplace.models.User;
import org.marketplace.services.UserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }


    @PostMapping("/add")
    public User requestAddUser(@RequestBody User user) {
        return userManagementService.registerNewUserAccount(user);
    }

    @GetMapping("/all")
    public List<User> getUsers()
    {
        return userManagementService.getUsers();
    }

}
