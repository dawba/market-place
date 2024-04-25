package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;

   // private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    //for encoding password
    /*
    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
    }
    */

    public User registerNewUserAccount(@Valid User user) {
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userManagementRepository.save(user);
    }

    public List<User> getUsers() {
        return userManagementRepository.findAll();
    }

    public User getUserById(Long id) {
        return userManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %d was not found", id)));
    }

}
