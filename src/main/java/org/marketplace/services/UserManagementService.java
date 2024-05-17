package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.enums.UserRole;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;

    private PasswordEncoder passwordEncoder;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUserAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userManagementRepository.save(user);
    }


    public List<User> getAllUsers() {
        return userManagementRepository.findAll();
    }

    public User getUserById(Long id) {
        return userManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %d was not found", id)));
    }

    public User updateUser(User user) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().equals(UserRole.USER) && !currentUser.getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this user's details.");
        }

        return registerNewUserAccount(user);
    }

    public void deleteUserById(Long id) {
        if (!userManagementRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("User with id: %d was not found", id));
        }
        User currentUser = getCurrentUser();
        if (currentUser.getRole().equals(UserRole.USER) && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You are not authorized to delete this user.");
        }

        userManagementRepository.deleteById(id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userManagementRepository.findByEmail(email);
        } else {
            return null;
        }
    }

}
