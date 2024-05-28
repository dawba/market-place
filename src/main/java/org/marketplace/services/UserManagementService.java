package org.marketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.marketplace.configuration.BaseJWT;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.requests.DuplicateEntryException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    private final UserManagementRepository userManagementRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUserAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            return userManagementRepository.save(user);
        }catch (DataIntegrityViolationException e)
        {
            if(userManagementRepository.findByEmail(user.getEmail())!=null)
                throw new DuplicateEntryException(String.format("Duplicate entry for email: %s", user.getEmail()));
            else
                throw new DuplicateEntryException(String.format("Duplicate entry for login: %s", user.getLogin()));
        }

    }


    public List<User> getAllUsers() {
        return userManagementRepository.findAll();
    }

    public User getUserById(Long id) {
        return userManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %d was not found", id)));
    }

    public User updateUser(User user) {
        User previousUser = userManagementRepository.findByEmail(user.getEmail());
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
