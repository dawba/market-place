package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;
    TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
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
        String previousUsername = tokenService.extractUsername(jwt);
        User previousUser = userManagementRepository.findByEmail(previousUsername);
        previousUser.setPassword(user.getPassword());
        previousUser.setLogin(user.getLogin());
        previousUser.setEmail(user.getEmail());
        previousUser.setPhoneNumber(user.getPhoneNumber());
        return registerNewUserAccount(previousUser);
    }

    public void deleteUserById(Long id) {
        userManagementRepository.deleteById(id);
    }
}
