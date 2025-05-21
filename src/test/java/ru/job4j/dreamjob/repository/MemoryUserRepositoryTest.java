package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryUserRepositoryTest {
    private static MemoryUserRepository repository;

    @BeforeAll
    public static void setUpBeforeClass() {
        repository = new MemoryUserRepository();
    }

    @BeforeEach
    public void clearMap() {
        repository.getUsers().clear();
    }

    @Test
    public void whenAddUserSuccess() {
        var user = new User(0, "Doe@gmail.com", "Doe", "123");
        var result = repository.save(user).get();
        assertThat(result).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenFindUserSuccess() {
        var user1 = new User(0, "Doe@gmail.com", "Doe", "123");
        var expected = repository.save(user1).get();
        var result = repository.findByEmailAndPassword(user1.getEmail(), user1.getPassword()).get();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenFindUserFailed() {
        var user1 = new User(0, "Doe@gmail.com", "Doe", "123");
        var expected = repository.save(user1).get();
        var result = repository.findByEmailAndPassword(null, null);
        assertThat(result).usingRecursiveComparison().isNotEqualTo(expected);
    }
}