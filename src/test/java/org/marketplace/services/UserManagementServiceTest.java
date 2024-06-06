package org.marketplace.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.builders.EmailBuilder;
import org.marketplace.enums.UserRole;
import org.marketplace.models.Email;
import org.marketplace.models.RegistrationToken;
import org.marketplace.models.User;
import org.marketplace.repositories.RegistrationTokenManagementRepository;
import org.marketplace.repositories.UserManagementRepository;
import org.marketplace.requests.DuplicateEntryException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

    @Mock
    private UserManagementRepository userManagementRepository;

    @Mock
    private RegistrationTokenManagementRepository registrationTokenManagementRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserManagementService userManagementService;

    private User user;
    private RegistrationToken registrationToken;

    @BeforeEach
    public void setUp() {
        user = new User(2L, "user2", "password", UserRole.USER, "user2@gmail.com", "123456789");
        registrationToken = new RegistrationToken(user);
    }

    @Test
    public void testRegisterNewUserAccount() {
        // given
        given(userManagementRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(userManagementRepository.existsByLogin(user.getLogin())).willReturn(false);
        given(passwordEncoder.encode(user.getPassword())).willReturn("encodedPassword");
        given(userManagementRepository.save(any(User.class))).willReturn(user);
        given(registrationTokenManagementRepository.save(any(RegistrationToken.class))).willReturn(registrationToken);

        // when
        User registeredUser = userManagementService.registerNewUserAccount(user);

        // then
        assertThat(registeredUser).isNotNull();
        verify(emailService, times(1)).sendEmail(any(Email.class));
        verify(userManagementRepository, times(1)).save(user);
        verify(registrationTokenManagementRepository, times(1)).save(any(RegistrationToken.class));
    }

    @Test
    public void testRegisterNewUserAccountWithExistingEmail() {
        // given
        given(userManagementRepository.existsByEmail(user.getEmail())).willReturn(true);

        // when
        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> {
            userManagementService.registerNewUserAccount(user);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("User with email: user2@gmail.com already exists");
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterNewUserAccountWithExistingLogin() {
        // given
        given(userManagementRepository.existsByLogin(user.getLogin())).willReturn(true);

        // when
        EntityExistsException thrown = assertThrows(EntityExistsException.class, () -> {
            userManagementService.registerNewUserAccount(user);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("User with login: user2 already exists");
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void testConfirmUserAccount() {
        // given
        given(registrationTokenManagementRepository.findByConfirmationToken(registrationToken.getConfirmationToken())).willReturn(registrationToken);
        given(userManagementRepository.findByEmailIgnoreCase(user.getEmail())).willReturn(user);
        given(userManagementRepository.save(user)).willReturn(user);

        // when
        User confirmedUser = userManagementService.confirmUserAccount(registrationToken.getConfirmationToken());

        // then
        assertThat(confirmedUser.isVerified()).isTrue();
        verify(userManagementRepository, times(1)).save(user);
    }

    @Test
    public void testConfirmUserAccountWithInvalidToken() {
        // given
        given(registrationTokenManagementRepository.findByConfirmationToken("invalidToken")).willReturn(null);

        // when
        AuthenticationServiceException thrown = assertThrows(AuthenticationServiceException.class, () -> {
            userManagementService.confirmUserAccount("invalidToken");
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Token not found, could not verify email.");
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void testConfirmUserAccountWithInvalidTokenFormat() {
        // given
        String invalidTokenFormat = "invalid-token-format";
        registrationToken.setConfirmationToken(invalidTokenFormat);
        given(registrationTokenManagementRepository.findByConfirmationToken(invalidTokenFormat)).willReturn(registrationToken);

        // when
        AuthenticationServiceException thrown = assertThrows(AuthenticationServiceException.class, () -> {
            userManagementService.confirmUserAccount(invalidTokenFormat);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("Forbidden token format, could not verify email");
        verify(userManagementRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        // given
        given(userManagementRepository.findAll()).willReturn(List.of(user));

        // when
        List<User> users = userManagementService.getAllUsers();

        // then
        assertThat(users).isNotEmpty();
        assertThat(users).contains(user);
    }

    @Test
    public void testGetUserById() {
        // given
        given(userManagementRepository.findById(2L)).willReturn(Optional.of(user));

        // when
        User foundUser = userManagementService.getUserById(2L);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(2L);
        assertThat(foundUser.getLogin()).isEqualTo("user2");
        assertThat(foundUser.getEmail()).isEqualTo("user2@gmail.com");
    }

    @Test
    public void testGetUserByIdNotFound() {
        // given
        given(userManagementRepository.findById(2L)).willReturn(Optional.empty());

        // when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            userManagementService.getUserById(2L);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("User with id: 2 was not found");
    }

    @Test
    public void testUpdateUserAccount() {
        // given
        given(userManagementRepository.findByEmail(user.getEmail())).willReturn(user);
        user.setLogin("newLogin");
        user.setPassword("newPassword");
        given(passwordEncoder.encode(user.getPassword())).willReturn("encodedPassword");
        given(userManagementRepository.save(user)).willReturn(user);

        // when
        User updatedUser = userManagementService.updateUserAccount(user);

        // then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(updatedUser.getLogin()).isEqualTo("newLogin");
        verify(userManagementRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUserById() {
        // given
        doNothing().when(userManagementRepository).deleteById(1L);

        // when
        userManagementService.deleteUserById(1L);

        // then
        verify(userManagementRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserByIdNotFound() {
        // given
        doThrow(new EntityNotFoundException("User with id: 2 was not found")).when(userManagementRepository).deleteById(2L);
        // when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            userManagementService.deleteUserById(2L);
        });

        // then
        assertThat(thrown.getMessage()).isEqualTo("User with id: 2 was not found");
    }
}
