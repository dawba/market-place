package org.marketplace.repositories;

import org.marketplace.models.RegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationTokenManagementRepository extends JpaRepository<RegistrationToken, Long>{
    RegistrationToken findByConfirmationToken(String confirmationToken);

}
