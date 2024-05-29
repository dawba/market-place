package org.marketplace.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenCache {
    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();
    Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public void saveToken(String username, String token){
        tokenCache.put(username, token);
        logger.info("Token added to cache for user: " + username);
    }

    public String getToken(String username){
        logger.info("Getting token for user: " + username);
        return tokenCache.get(username);
    }

    public void removeToken(String username){
        tokenCache.remove(username);
        logger.info("Token removed from cache for user: " + username);
    }
}
