package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.configuration.BaseJWT;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;
    BaseJWT baseJWT;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationUserUtil authorizationUserUtil;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder, AuthorizationUserUtil authorizationUserUtil, BaseJWT baseJWT) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorizationUserUtil = authorizationUserUtil;
        this.baseJWT = baseJWT;
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

    public User updateUser(User user, String token) {
        String jwt = token.substring(7);
        String extractedEmail = baseJWT.extractUsername(jwt);
        if (!extractedEmail.equals(user.getEmail())) {
            throw new AccessDeniedException("You are not authorized to update this user's details.");
        }
        User previousUser = userManagementRepository.findByEmail(extractedEmail);
        previousUser.setPassword(user.getPassword());
        previousUser.setLogin(user.getLogin());
        previousUser.setEmail(user.getEmail());
        previousUser.setPhoneNumber(user.getPhoneNumber());
        return registerNewUserAccount(previousUser);
    }

    public void deleteUserById(Long id, String token) {
        User user = getUserById(id);
        if (!authorizationUserUtil.checkAccessToUserByCurrentUser(token, user)) {
            throw new AccessDeniedException(String.format("You are not authorized to delete user with id: .", id));
        }

        userManagementRepository.deleteById(id);
    }
}
