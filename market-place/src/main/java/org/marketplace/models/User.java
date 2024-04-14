package org.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Size(min = 5,max = 20, message = "Login must be between 5 and 20 characters long")
    private String login;
    @Email(message = "Invalid email address")
    private String email;
    @Pattern(regexp="^[1-9][0-9]{8}$", message="Phone number must have 9 digits and not start with 0")
    private String phoneNumber;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Advertisement> advertisements = new ArrayList<>();



}
