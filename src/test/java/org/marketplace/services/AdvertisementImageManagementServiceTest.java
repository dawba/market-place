package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.models.Advertisement;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.repositories.AdvertisementImageManagementRepository;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementImageManagementServiceTest {

    @Mock
    private AdvertisementImageManagementRepository advertisementImageManagementRepository;

    @Mock
    private AdvertisementManagementRepository advertisementManagementRepository;

    @InjectMocks
    private AdvertisementImageManagementService advertisementImageManagementService;


    private AdvertisementImage advertisementImage;
    private Advertisement advertisement;

    @BeforeEach
    void setUp() {
        advertisement = new Advertisement();
        advertisementImage = new AdvertisementImage(1L, "filePath", advertisement);
    }

    @Test
    void testAddImage_ShouldAddImage() {
        // given
        given(advertisementManagementRepository.existsById(advertisement.getId())).willReturn(true);
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.empty());
        given(advertisementImageManagementRepository.save(any(AdvertisementImage.class))).willReturn(advertisementImage);

        // when
        AdvertisementImage savedImage = advertisementImageManagementService.addImage(advertisementImage);

        // then
        assertThat(savedImage).isNotNull();
        verify(advertisementImageManagementRepository, times(1)).save(advertisementImage);
    }


    @Test
    void testAddImage_ShouldThrowExceptionWhenImageExists() {
        // given
        given(advertisementManagementRepository.existsById(advertisement.getId())).willReturn(true);
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.of(advertisementImage));

        // when
        assertThrows(EntityExistsException.class, () -> advertisementImageManagementService.addImage(advertisementImage));

        // then
        verify(advertisementImageManagementRepository, never()).save(any(AdvertisementImage.class));
    }

    @Test
    void testGetAdvertisementImageById_ShouldReturnImageWhenImageExists() {
        // given
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.of(advertisementImage));

        // when
        AdvertisementImage foundImage = advertisementImageManagementService.getAdvertisementImageById(advertisementImage.getId());

        // then
        assertThat(foundImage).isNotNull();
        assertThat(foundImage.getId()).isEqualTo(advertisementImage.getId());
    }

    @Test
    void testGetAdvertisementImageById_ShouldThrowExceptionWhenImageDoesNotExist() {
        // given
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.empty());

        // when
        assertThrows(EntityNotFoundException.class, () -> advertisementImageManagementService.getAdvertisementImageById(advertisementImage.getId()));
    }

    @Test
    void testUpdateAdvertisementImage_ShouldUpdateImageWhenImageExists() {
        // given
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.of(advertisementImage));
        advertisementImage.setFilepath("updatedFilePath");
        given(advertisementImageManagementRepository.save(any(AdvertisementImage.class))).willReturn(advertisementImage);
        // when
        AdvertisementImage updatedImage = advertisementImageManagementService.updateAdvertisementImage(advertisementImage);

        // then
        assertThat(updatedImage).isNotNull();
        assertThat(updatedImage.getFilepath()).isEqualTo("updatedFilePath");
        verify(advertisementImageManagementRepository, times(1)).save(advertisementImage);
    }

    @Test
    void testUpdateAdvertisementImage_ShouldThrowExceptionWhenImageDoesNotExist() {
        // given
        given(advertisementImageManagementRepository.findById(advertisementImage.getId())).willReturn(Optional.empty());

        // when
        assertThrows(EntityNotFoundException.class, () -> advertisementImageManagementService.updateAdvertisementImage(advertisementImage));
    }

    @Test
    void testDeleteAdvertisementImage_ShouldDeleteImageWhenImageExists() {
        // given
        given(advertisementImageManagementRepository.existsById(advertisementImage.getId())).willReturn(true);

        // when
        advertisementImageManagementService.deleteAdvertisementImage(advertisementImage.getId());

        // then
        verify(advertisementImageManagementRepository, times(1)).deleteById(advertisementImage.getId());
    }

    @Test
    void testDeleteAdvertisementImage_ShouldThrowExceptionWhenImageDoesNotExist() {
        // given
        given(advertisementImageManagementRepository.existsById(advertisementImage.getId())).willReturn(false);

        // when
        assertThrows(EntityNotFoundException.class, () -> advertisementImageManagementService.deleteAdvertisementImage(advertisementImage.getId()));
    }

    @Test
    void testGetAllAdvertisementImages_ShouldReturnImagesForGivenAdvertisementId() {
        // given
        List<AdvertisementImage> images = List.of(advertisementImage);
        given(advertisementImageManagementRepository.findByAdvertisement_Id(advertisement.getId())).willReturn(images);

        // when
        List<AdvertisementImage> foundImages = advertisementImageManagementService.getAllAdvertisementImages(advertisement.getId());

        // then
        assertThat(foundImages).isNotNull();
        assertThat(foundImages.size()).isEqualTo(1);
    }
}
