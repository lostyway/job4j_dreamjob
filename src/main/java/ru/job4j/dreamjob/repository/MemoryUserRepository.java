package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryUserRepository implements UserRepository {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();

    public MemoryUserRepository() {
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return users;
    }

    @Override
    public Optional<User> save(User user) {
        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
                .findFirst();
    }
}
