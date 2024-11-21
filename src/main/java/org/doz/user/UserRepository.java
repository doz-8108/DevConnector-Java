package org.doz.user;

import org.doz.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    <T> Optional<T> findByEmail(String email, Class<T> type);
}