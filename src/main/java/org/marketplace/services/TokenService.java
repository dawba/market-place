package org.marketplace.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.marketplace.cache.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService {
    @Autowired
    private TokenCache tokenCache;

    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        return Jwts.builder().setClaims(mutableClaims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * Generate a token for the given username or return an existing one if it is not expired
     * @param username
     * @return token
     */
    public String generateToken(String username){
        String token = tokenCache.getToken(username);
        boolean isTokenExpired = token != null && isTokenExpired(token);

        if(token != null && !isTokenExpired){
            return token;
        }

        if(token != null){
            tokenCache.removeToken(username);
        }

        token = createToken(new HashMap<>(), username);
        tokenCache.saveToken(username, token);
        return token;
    }

    /**
     * Validate the token for the given user
     * @param token token to validate
     * @param userDetails user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Invalidate the token for the given user
     * @param token
     * @throws BadCredentialsException if the token is invalid
     */
    public void invalidateToken(String token) {
        String username = extractUsername(token);

        if (username == null) {
            throw new BadCredentialsException("Invalid token provided.");
        }

        String cachedToken = tokenCache.getToken(username);

        if (cachedToken == null || isTokenExpired(cachedToken)) {
            throw new BadCredentialsException("User with the username " + username + " is not logged in or has an expired token.");
        }

        tokenCache.removeToken(username);
    }
}
