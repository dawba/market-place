package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.builders.EmailBuilder;
import org.marketplace.models.Email;
import org.marketplace.models.RegistrationToken;
import org.marketplace.models.User;
import org.marketplace.repositories.RegistrationTokenManagementRepository;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.requests.DuplicateEntryException;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {
    @Autowired
    private final UserManagementRepository userManagementRepository;
    TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    RegistrationTokenManagementRepository registrationTokenManagementRepository;

    @Autowired
    EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordEncoder passwordEncoder, TokenService tokenService, EmailService emailService, RegistrationTokenManagementRepository registrationTokenManagementRepository) {
        this.userManagementRepository = userManagementRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.registrationTokenManagementRepository = registrationTokenManagementRepository;
        this.emailService = emailService;
    }

    public User registerNewUserAccount(User user) {
        if (userManagementRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException(String.format("User with email: %s already exists", user.getEmail()));
        }

        if(userManagementRepository.existsByLogin(user.getLogin())) {
            throw new EntityExistsException(String.format("User with login: %s already exists", user.getLogin()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        RegistrationToken registrationToken = new RegistrationToken(user);
        logger.info("User object: " + user);

        Email mailMessage = new EmailBuilder()
                .setTo(user.getEmail())
                .setSubject("Complete your registration for marketplace!")
                .setContent("Hello " + user.getLogin() + ",\n\nThank you for registering for our platform! \n"
                        +"To confirm your account, please click here : "
                        + "http://localhost:8080/api/user/confirm-account?token=" + registrationToken.getConfirmationToken())
                .build();

        emailService.sendEmail(mailMessage);
        userManagementRepository.save(user);
        registrationTokenManagementRepository.save(registrationToken);

        logger.info("User created: " + user);
        logger.info("Registration token for the user: " + registrationToken.getConfirmationToken());
        return user;
    }

    public User confirmUserAccount(String token) {
        String trimmedToken = token.trim();
        RegistrationToken registrationToken = registrationTokenManagementRepository.findByConfirmationToken(trimmedToken);

        if (registrationToken == null) {
            throw new AuthenticationServiceException("Token not found, could not verify email.");
        }

        String tokenRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
        if (!token.trim().matches(tokenRegex)) {
            throw new AuthenticationServiceException("Forbidden token format, could not verify email");
        }

        User user = userManagementRepository.findByEmailIgnoreCase(registrationToken.getUser().getEmail());
        user.setVerified(true);
        userManagementRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userManagementRepository.findAll();
    }

    public User getUserById(Long id) {
        return userManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %d was not found", id)));
    }

    public User updateUserAccount(User user) {
        User previousUser = userManagementRepository.findByEmail(user.getEmail());
        previousUser.setPassword(passwordEncoder.encode(user.getPassword()));
        previousUser.setLogin(user.getLogin());
        previousUser.setEmail(user.getEmail());
        previousUser.setPhoneNumber(user.getPhoneNumber());
        return updateUser(previousUser);
    }

    private User updateUser(User user) {
        try {
            return userManagementRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (userManagementRepository.existsByEmail(user.getEmail()))
                throw new DuplicateEntryException(String.format("Duplicate entry for email: %s", user.getEmail()));

            throw new DuplicateEntryException(String.format("Duplicate entry for login: %s", user.getLogin()));
        }
    }

    public void deleteUserById(Long id) {
        userManagementRepository.deleteById(id);
    }
}
