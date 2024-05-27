package org.marketplace.config;

import org.springframework.cache.Cache;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;

import java.io.Serializable;

public class SpringCacheBasedAclCache implements AclCache {


    private final Cache cache;
    private final DefaultPermissionGrantingStrategy permissionGrantingStrategy;
    private final AclAuthorizationStrategy aclAuthorizationStrategy;

    public SpringCacheBasedAclCache(Cache cache, DefaultPermissionGrantingStrategy permissionGrantingStrategy, AclAuthorizationStrategy aclAuthorizationStrategy) {
        this.cache = cache;
        this.permissionGrantingStrategy = permissionGrantingStrategy;
        this.aclAuthorizationStrategy = aclAuthorizationStrategy;
    }

    @Override
    public void evictFromCache(Serializable pk) {
        cache.evict(pk);
    }

    @Override
    public void evictFromCache(ObjectIdentity objectIdentity) {
        cache.evict(objectIdentity);
    }

    @Override
    public MutableAcl getFromCache(ObjectIdentity objectIdentity) {
        return cache.get(objectIdentity, MutableAcl.class);
    }

    @Override
    public MutableAcl getFromCache(Serializable pk) {
        return cache.get(pk, MutableAcl.class);
    }

    @Override
    public void putInCache(MutableAcl acl) {
        cache.put(acl.getObjectIdentity(), acl);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}