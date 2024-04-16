package org.marketplace.services;

import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;

    public UserManagementService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    public User addUser(User user){
        return userManagementRepository.save(user);
    }
}
