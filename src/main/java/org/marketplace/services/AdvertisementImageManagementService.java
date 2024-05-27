package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdvertisementImageManagementService {
    private final AdvertisementImageManagementRepository advertisementImageManagementRepository;

    private final MutableAclService aclService;

    public AdvertisementImageManagementService(AdvertisementImageManagementRepository advertisementImageManagementRepository, MutableAclService aclService) {
        this.advertisementImageManagementRepository = advertisementImageManagementRepository;
        this.aclService = aclService;
    }

    public AdvertisementImage addImage(AdvertisementImage advertisementImage) {
        try {
            //getAdvertisementImageById(advertisementImage.getId());
            //throw new EntityExistsException(String.format("Advertisement image with id: %d already exists!", advertisementImage.getId()));
            return save(advertisementImage);
        } catch (EntityNotFoundException e) {
            return save(advertisementImage);
        }
    }

    public AdvertisementImage getAdvertisementImageById(Long id) {
        return advertisementImageManagementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", id)));
    }

    public AdvertisementImage updateAdvertisementImage(AdvertisementImage advertisementImage) {
        return advertisementImageManagementRepository.findById(advertisementImage.getId())
                .map(c -> save(advertisementImage))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", advertisementImage.getId())));
    }

    public void deleteAdvertisementImage(Long id) {
        if (!advertisementImageManagementRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Advertisement image with id: %d was not found", id));
        }

        advertisementImageManagementRepository.deleteById(id);
    }

    public List<AdvertisementImage> getAllAdvertisementImages(Long advertisementId) {
        return advertisementImageManagementRepository.findByAdvertisement_Id(advertisementId);
    }

    @Transactional
    public AdvertisementImage save(AdvertisementImage document) {
        AdvertisementImage savedDocument = advertisementImageManagementRepository.save(document);
        MutableAcl acl = setUpAcl(savedDocument);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        acl.insertAce(0, BasePermission.ADMINISTRATION, new PrincipalSid(username), true);
        acl.insertAce(1, BasePermission.READ, new PrincipalSid(username), true);
        acl.insertAce(2, BasePermission.WRITE, new PrincipalSid(username), true);
        aclService.updateAcl(acl);

        return savedDocument;
    }

    @Transactional
    public MutableAcl setUpAcl(AdvertisementImage savedDocument) {
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(AdvertisementImage.class, savedDocument.getId());
        MutableAcl acl = aclService.createAcl(objectIdentity);
        return acl;
    }
}
