package org.marketplace.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketplace.cache.TokenCache;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TokenServiceTest {
    private final String username = "testUser";
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private TokenCache tokenCache;
    @InjectMocks
    private TokenService tokenService;

    @Before
    public void setUp() {
    }

    @Test
    public void generateToken_notLoggedIn() {
        String token = tokenService.generateToken(username);
        assertNotNull(token);
    }

    @Test
    public void generateToken_loggedIn() {
        String token = tokenService.generateToken(username);
        assertNotNull(token);

        String token2 = tokenService.generateToken(username);
        assertEquals(token, token2);
    }

    @Test
    public void generateToken_expiredToken() {
        String token = tokenService.generateToken(username);
        assertNotNull(token);

        tokenService.invalidateToken(token);

        String token2 = tokenService.generateToken(username);
        assertNotEquals(token, token2);
    }

    @Test
    public void validateToken() {
        String token = tokenService.generateToken(username);
        assertNotNull(token);

        UserDetails userDetails = User.withUsername(username).password("password").authorities("USER").build();
        UserDetails differentUserDetails = User.withUsername("differentUser").password("password").authorities("USER").build();
        assertTrue(tokenService.validateToken(token, userDetails));
        assertFalse(tokenService.validateToken(token, differentUserDetails));
    }

    @Test
    public void validateToken_emptyToken() {
        String token = "";
        UserDetails userDetails = User.withUsername(username).password("password").authorities("USER").build();
        assertFalse(tokenService.validateToken(token, userDetails));
    }

    @Test
    public void invalidateToken_validToken() {
        String token = tokenService.generateToken(username);
        assertNotNull(token);

        tokenService.invalidateToken(token);
        verify(tokenCache, times(1)).removeToken(username);
    }

    @Test(expected = BadCredentialsException.class)
    public void invalidateToken_invalidToken() {
        String invalidToken = "invalidToken";
        when(tokenCache.getToken(username)).thenReturn(null);
        tokenService.invalidateToken(invalidToken);
    }


    @Test
    public void testOnLogoutSuccess_validToken() throws BadCredentialsException {
        // Test the onLogoutSuccess method with a valid token
    }
}
