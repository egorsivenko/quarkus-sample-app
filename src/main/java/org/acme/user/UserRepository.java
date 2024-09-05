package org.acme.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    ID save(T t);

    void update(ID id, T t);

    void delete(ID id);
}
