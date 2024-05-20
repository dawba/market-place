package org.marketplace.services;

import org.marketplace.configuration.BaseJWT;
import org.marketplace.enums.UserRole;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUserUtil {

    private BaseJWT baseJWT;

    private UserManagementRepository userManagementRepository;

    public AuthorizationUserUtil(BaseJWT baseJWT, UserManagementRepository userManagementRepository) {
        this.baseJWT = baseJWT;
        this.userManagementRepository = userManagementRepository;
    }

    //check if current user is admin or current user is provided user
    public boolean checkAccessToUserByCurrentUser(String token, User user)
    {
        String email = user.getEmail();
        String jwt = token.substring(7);
        String extractedEmail = baseJWT.extractUsername(jwt);
        return extractedEmail.equals(email)||getUserByEmail(extractedEmail).getRole().equals(UserRole.ADMIN);
    }

    private User getUserByEmail(String email) {
        return userManagementRepository.findByEmail(email);
    }
}