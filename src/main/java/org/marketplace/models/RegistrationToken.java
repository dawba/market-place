package org.marketplace.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;


@Entity
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="confirmation_token_id")
    private Long confirmationTokenId;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public RegistrationToken() {
    }

    public RegistrationToken(User user) {
        this.user = user;
        this.confirmationToken = UUID.randomUUID().toString();
        this.createdDate = new Date();
    }

    public Long getConfirmationTokenId() {
        return confirmationTokenId;
    }

    public void setConfirmationTokenId(Long confirmationTokenId) {
        this.confirmationTokenId = confirmationTokenId;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
