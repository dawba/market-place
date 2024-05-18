package org.marketplace.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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
    private BaseJWT baseJWT;

    public UserPassRequestFilter(String loginUrl, AuthenticationManager authenticationManager) {
        super(loginUrl);
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String source = IOUtils.toString(request.getReader());
        if (source == null || source.isEmpty()) {
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
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authRequest);
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String jwt = baseJWT.generateToken(authResult.getName());
        response.addHeader("Authorization", "Bearer " + jwt);
    }
}