package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InMemoryUserRepository implements UserRepository<User, UUID> {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return Optional.ofNullable(users.get(uuid));
    }

    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return users.containsKey(uuid);
    }

    public boolean existsByEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public UUID save(User user) {
        UUID uuid = UUID.randomUUID();
        users.put(uuid, user);
        return uuid;
    }

    @Override
    public void update(UUID uuid, User user) {
        users.replace(uuid, user);
    }

    @Override
    public void delete(UUID uuid) {
        users.remove(uuid);
    }
}
