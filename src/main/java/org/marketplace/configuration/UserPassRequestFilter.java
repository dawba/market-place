package org.marketplace.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.cache.TokenCache;
import org.marketplace.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserPassRequestFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private TokenService tokenService;
    Logger logger = LoggerFactory.getLogger(UserPassRequestFilter.class);

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;


    public UserPassRequestFilter(String loginUrl, AuthenticationManager authenticationManager, CustomAuthenticationFailureHandler failureHandler) {
        super(loginUrl);
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl));
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String source = IOUtils.toString(request.getReader());
        if (source == null || source.isEmpty()) {
            failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("Invalid request"));
            return null;
        }
        JSONObject creds;
        String username;
        String password;
        try {
            creds = new JSONObject(source);
            username = creds.getString("username");
            password = creds.getString("password");
        } catch (JSONException e) {
            failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("Invalid JSON"));
            return null;
        }

        User user = userManagementRepository.findByEmailIgnoreCase(username);
        if (user == null) {
            failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("User not found"));
            return null;
        }

        if (!user.isVerified()) {
            failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("Account is not verified"));
            return null;
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String jwt = tokenService.generateToken(authResult.getName());
        response.addHeader("Authorization", "Bearer " + jwt);
        response.setContentType("application/json");
        response.getWriter().write("{\"Bearer\": \"" + jwt + "\"}");
        response.setStatus(HttpServletResponse.SC_OK);
        logger.info("Authentication successful");
    }
}