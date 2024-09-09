package org.acme.user.repository;

import org.acme.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    void save(User user);

    void update(UUID id, User user);

    void delete(UUID id);
}
