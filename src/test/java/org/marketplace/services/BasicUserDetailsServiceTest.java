package org.marketplace.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.configuration.CustomUserDetails;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BasicUserDetailsServiceTest {

    @Mock
    private UserManagementRepository userManagementRepository;

    @InjectMocks
    private BasicUserDetailsService basicUserDetailsService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        given(userManagementRepository.findByEmail(email)).willReturn(user);

        // When
        CustomUserDetails userDetails = (CustomUserDetails) basicUserDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Given
        String email = "nonexistent@example.com";
        given(userManagementRepository.findByEmail(email)).willReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> basicUserDetailsService.loadUserByUsername(email));
        assertEquals(String.format("User does not exist with email: %s", email), exception.getMessage());
    }
}