package org.marketplace.requests;

import org.marketplace.cache.TokenCache;
import org.marketplace.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private final TokenService tokenService;
    Logger logger = LoggerFactory.getLogger(LogoutSuccessHandler.class);

    public LogoutSuccessHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new BadCredentialsException("Token is missing or invalid.");
            }

            String token = authorizationHeader.substring(7);
            tokenService.invalidateToken(token);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Logout successful.");
            logger.info("Logout successful");
        } catch (BadCredentialsException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            logger.error("Logout failed: " + ex.getMessage());
        }
    }
}