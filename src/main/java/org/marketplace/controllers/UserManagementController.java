package org.marketplace.controllers;

import org.marketplace.models.User;
import org.marketplace.services.UserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }


    @PostMapping
    public User requestAddUser(@RequestBody User user) {
        return userManagementService.addUser(user);
    }

    @GetMapping("/test")
    public User getUser()
    {
        System.out.println("Testing getMapping User Controller");
        return new User();
    }

}
