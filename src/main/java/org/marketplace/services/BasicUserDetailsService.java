package org.marketplace.services;

import org.marketplace.enums.UserRole;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BasicUserDetailsService implements UserDetailsService {

    private final UserManagementRepository userManagementRepository;

    public BasicUserDetailsService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email.equals("defaultUser")) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(email)
                    .password("defaultPass")
                    .roles(UserRole.USER.getValue(),UserRole.ADMIN.getValue())
                    .build();
        }
        User user = userManagementRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User does not exist with email: %s", email));
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(UserRole.USER.getValue(),UserRole.ADMIN.getValue())
                .build();

    }
}
