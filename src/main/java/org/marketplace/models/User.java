package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.marketplace.enums.UserRole;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;
    @Column(unique = true)
    @Size(min = 5, max = 20, message = "Login must be between 5 and 20 characters long")
    private String login;
    @NotNull
    private String password;

    private UserRole role;
    @Column(unique = true)
    @Email(message = "Invalid email address")
    private String email;
    @Pattern(regexp = "^[1-9][0-9]{8}$", message = "Phone number must have 9 digits and not start with 0")
    private String phoneNumber;

    public User(Long id, String login, String password, UserRole role, String email, String phoneNumber) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User() {

    }

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
