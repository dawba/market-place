package org.marketplace.repositories;

import jakarta.annotation.Nullable;
import org.marketplace.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<User, Long> {
}
