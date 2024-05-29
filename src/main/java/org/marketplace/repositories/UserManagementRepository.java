package org.marketplace.repositories;

import org.marketplace.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByLogin(String login);
    User findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
    Boolean existsByLogin(String login);
}
