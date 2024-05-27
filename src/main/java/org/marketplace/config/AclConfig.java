package org.marketplace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.cache.CacheManager;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableCaching
public class AclConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public MutableAclService mutableAclService(AclCache aclCache) {
        return new JdbcMutableAclService(dataSource, lookupStrategy(aclCache), aclCache);
    }

    @Bean
    public BasicLookupStrategy lookupStrategy(AclCache aclCache) {
        return new BasicLookupStrategy(dataSource, aclCache, aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    @Bean
    public AclCache aclCache(CacheManager cacheManager) {
        return new SpringCacheBasedAclCache(
                cacheManager.getCache("aclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @Bean
    public JCacheCacheManager jCacheCacheManager() throws IOException {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(
                new ClassPathResource("ehcache.xml").getURI(),
                getClass().getClassLoader()
        );
        return new JCacheCacheManager(cacheManager);
    }

    @Bean
    public DefaultPermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategyImpl aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
