package org.marketplace.services;

import org.marketplace.enums.AccessStatus;
import org.marketplace.enums.ResourceType;
import org.marketplace.models.Advertisement;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.models.User;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.requests.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ResourceAccessAuthorizationService {
    @Autowired
    private final UserManagementRepository userManagementRepository;
    @Autowired
    private AdvertisementManagementRepository advertisementManagementRepository;
    @Autowired
    private AdvertisementImageManagementRepository advertisementImageManagementRepository;
    private static final Logger logger = LoggerFactory.getLogger(ResourceAccessAuthorizationService.class);


    @Autowired
    public ResourceAccessAuthorizationService(UserManagementRepository userManagementRepository, AdvertisementManagementRepository advertisementManagementRepository, AdvertisementImageManagementRepository advertisementImageManagementRepository) {
        this.userManagementRepository = userManagementRepository;
        this.advertisementManagementRepository = advertisementManagementRepository;
        this.advertisementImageManagementRepository = advertisementImageManagementRepository;
    }

    /**
     * Method for authorizing access for HTTP requests that
     * modify the resources, with intention to prevent unauthorized access to the resources.
     * If the user does not have access to the resource, the method throws a UserNotFoundException.
     * @param resource type of the resource
     *                 (USER, ADVERTISEMENT, ADVERTISEMENT_IMAGE)
     *                 that is being accessed
     * @param resourceId id of the resource that is being accessed
     *                   (user id, advertisement id, advertisement image id)
     *                   for which access is being authorized
     *
     * @throws UserNotFoundException if the user does not have access to the resource
     * @return AccessStatus that indicates user access status
     * @see #authorizeUserAccessFromRequestBodyOrThrow(ResourceType, Long) AuthorizeUserAccessFromRequestBody method if needed to authorize access from the request body
     */
    public AccessStatus authorizeUserAccess(ResourceType resource, Long resourceId) {
        Long resourceOwnerId = fetchOwnerId(resource, resourceId);

        if (resourceOwnerId == null) {
            logger.info("Resource owner not found for resource id: " + resourceId + " and resource type: " + resource);
            throw new UserNotFoundException("User not found for resource id: " + resourceId + " and resource type: " + resource);
        }

        User currentUser = extractCurrentUserFromAuth();

        if(Objects.equals(resourceOwnerId, currentUser.getId()) || currentUser.isAdmin()){
            logger.info("User has access to the resource, access granted. Proceeding with the request to " + resource + " service.");
            return AccessStatus.ACCESS_GRANTED;
        }

        logger.info("User does not have access to the resource, access denied.");
        return AccessStatus.ACCESS_DENIED;
    }

    /**
     * Method for authorizing access for HTTP requests that
     * modify the resources, with intention to prevent unauthorized access to the resources.
     * For authorizing access, the method uses the request body to fetch the resource id.
     * If the user does not have access to the resource, the method throws a UserNotFoundException.
     *
     * @param resource type of the resource
     * @param resourceId id of the resource
     * @throws UserNotFoundException if the user does not have access to the resource
     * @see #authorizeUserAccess(ResourceType, Long) AuthorizeUserAccess method if needed to authorize access just from the resourceId
     */
    public void authorizeUserAccessFromRequestBodyOrThrow(ResourceType resource, Long resourceId) {
        AccessStatus accessStatus = authorizeUserAccess(resource, resourceId);

        if(accessStatus == AccessStatus.ACCESS_DENIED){
            throw new UserNotFoundException("User not found for resource id: " + resourceId + " and resource type: " + resource);
        }
    }

    private User extractCurrentUserFromAuth(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userManagementRepository.findByEmail(username);
    }

    private Long fetchOwnerId(ResourceType resource, Long resourceId) {
        switch (resource) {
            case USER:
                return resourceId;
            case ADVERTISEMENT:
                return getOwnerIdFromAdvertisement(resourceId);
            case ADVERTISEMENT_IMAGE:
                AdvertisementImage image = advertisementImageManagementRepository.findById(resourceId).orElse(null);

                if(image == null){
                    return null;
                }

                return getOwnerIdFromAdvertisement(image.getAdvertisement().getId());
            default:
                return null;
        }
    }

    private Long getOwnerIdFromAdvertisement(Long advertisementId) {
        if(advertisementId == null){
            return null;
        }

        Advertisement ad = advertisementManagementRepository.findById(advertisementId).orElse(null);
        return ad != null ? ad.getOwner().getId() : null;
    }
}
