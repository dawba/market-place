package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.enums.AdvertisementStatus;
import org.marketplace.enums.UserRole;
import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.models.User;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.marketplace.specifications.AdvertisementSpecification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementManagementServiceTest {

    @Mock
    private AdvertisementManagementRepository advertisementManagementRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdvertisementManagementService advertisementManagementService;

    private Advertisement advertisement;
    private User user;
    private Category category;

    @BeforeEach
    public void setUp() {
        user = new User(2L, "user2", "password", UserRole.USER, "user2@gmail.com", "123456789");
        category = new Category(1L, "newCategory");
        advertisement = new Advertisement(1L, "Title", "Description", category, user, 100.0, "Location");
    }

    @Test
    public void testAddAdvertisement() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.empty());
        given(advertisementManagementRepository.save(advertisement)).willReturn(advertisement);

        // when
        Advertisement savedAd = advertisementManagementService.addAdvertisement(advertisement);

        // then
        assertThat(savedAd).isNotNull();
        assertThat(savedAd.getId()).isEqualTo(1L);
        verify(advertisementManagementRepository, times(1)).save(advertisement);
    }

    @Test
    public void testAddAdvertisementAlreadyExists() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));

        // when
        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> {
            advertisementManagementService.addAdvertisement(advertisement);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Advertisement with id: 1 already exists!");
        verify(advertisementManagementRepository, never()).save(any(Advertisement.class));
    }

    @Test
    public void testUpdateAdvertisement() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        advertisement.setPrice(150);
        given(advertisementManagementRepository.save(advertisement)).willReturn(advertisement);
        // when
        Advertisement updatedAd = advertisementManagementService.updateAdvertisement(advertisement);

        // then
        assertThat(updatedAd).isNotNull();
        assertThat(updatedAd.getId()).isEqualTo(1L);
        assertThat(updatedAd.getPrice()).isEqualTo(150);
        verify(advertisementManagementRepository, times(1)).save(advertisement);
    }

    @Test
    public void testUpdateAdvertisementNotFound() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.empty());

        // when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            advertisementManagementService.updateAdvertisement(advertisement);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Advertisement with id: 1 was not found");
        verify(advertisementManagementRepository, never()).save(any(Advertisement.class));
    }

    @Test
    public void testDeleteAdvertisement() {
        given(advertisementManagementRepository.existsById(advertisement.getId())).willReturn(true);
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        doNothing().when(advertisementManagementRepository).deleteById(advertisement.getId());

        // when
        advertisementManagementService.deleteAdvertisement(advertisement.getId());

        // then
        verify(advertisementManagementRepository, times(1)).deleteById(advertisement.getId());
    }

    @Test
    public void testDeleteAdvertisementNotFound() {
        // given
        given(advertisementManagementRepository.existsById(advertisement.getId())).willReturn(false);

        // when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            advertisementManagementService.deleteAdvertisement(advertisement.getId());
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Advertisement with id: 1 was not found");
        verify(advertisementManagementRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testGetAllAdvertisements() {
        // given
        given(advertisementManagementRepository.findAll()).willReturn(List.of(advertisement));

        // when
        List<Advertisement> advertisements = advertisementManagementService.getAllAdvertisements();

        // then
        assertThat(advertisements).isNotEmpty();
        assertThat(advertisements).contains(advertisement);
    }

    @Test
    public void testGetAdvertisementById() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));

        // when
        Advertisement foundAd = advertisementManagementService.getAdvertisementById(advertisement.getId());

        // then
        assertThat(foundAd).isNotNull();
        assertThat(foundAd.getId()).isEqualTo(1L);
        assertThat(foundAd.getTitle()).isEqualTo(advertisement.getTitle());
    }

    @Test
    public void testGetAdvertisementByIdNotFound() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.empty());

        // when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            advertisementManagementService.getAdvertisementById(advertisement.getId());
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Advertisement with id: 1 was not found");
    }

    @Test
    public void testBuyAdvertisement() {
        // given
        Long buyerId = 3L;
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        given(advertisementManagementRepository.save(any(Advertisement.class))).willReturn(advertisement);

        // when
        Advertisement boughtAd = advertisementManagementService.buyAdvertisement(advertisement.getId(), buyerId);

        // then
        assertThat(boughtAd.getStatus()).isEqualTo(AdvertisementStatus.BOUGHT);
        assertThat(boughtAd.getBuyerId()).isEqualTo(buyerId);
        verify(advertisementManagementRepository, times(1)).save(advertisement);
    }

    @Test
    void testObserveAdvertisement() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        given(advertisementManagementRepository.save(any(Advertisement.class))).willReturn(advertisement);

        // when
        Advertisement result = advertisementManagementService.observeAdvertisement(advertisement.getId(), "test@example.com");

        // then
        assertThat(result.getObservers()).hasSize(1).contains("test@example.com");
    }

    @Test
    void testObserveAdvertisementAlreadyObserved() {
        // given
        advertisement.getObservers().add("test@example.com");
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));

        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> {
            advertisementManagementService.observeAdvertisement(advertisement.getId(), "test@example.com");
        });

        assertThat(thrown.getMessage()).isEqualTo ("User with email: test@example.com already observes the advertisement with id: 1");
    }

    @Test
    void testUnobserveAdvertisement() {
        // given
        advertisement.getObservers().add("test@example.com");
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        given(advertisementManagementRepository.save(any(Advertisement.class))).willReturn(advertisement);

        // when
        Advertisement result = advertisementManagementService.unobserveAdvertisement(advertisement.getId(), "test@example.com");

        // then
        assertThat(result.getObservers()).doesNotContain("test@example.com");
    }

    @Test
    void testUnobserveAdvertisementNotObserved() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            advertisementManagementService.unobserveAdvertisement(advertisement.getId(), "test@example.com");
        });

        assertThat(thrown.getMessage()).isEqualTo ("User with email: test@example.com does not observe the advertisement with id: 1");

    }

    @Test
    void testChangeAdvertisementStatus() {
        // given
        given(advertisementManagementRepository.findById(advertisement.getId())).willReturn(Optional.of(advertisement));
        given(advertisementManagementRepository.save(any(Advertisement.class))).willReturn(advertisement);

        // when
        Advertisement result = advertisementManagementService.changeAdvertisementStatus(advertisement.getId(), "INACTIVE");

        // then
        assertThat(result.getStatus()).isEqualTo(AdvertisementStatus.INACTIVE);
    }

    @Test
    void testChangeAdvertisementStatusInvalidStatus() {

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            advertisementManagementService.changeAdvertisementStatus(advertisement.getId(), "INVALID_STATUS");
        });

        assertThat(thrown.getMessage()).isEqualTo ("Invalid status provided");

    }
}
