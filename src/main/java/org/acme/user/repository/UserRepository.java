package org.acme.user.repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> findByEmail(String email);

    boolean existsById(ID id);

    boolean existsByEmail(String email);

    ID save(T t);

    void update(ID id, T t);

    void delete(ID id);
}
