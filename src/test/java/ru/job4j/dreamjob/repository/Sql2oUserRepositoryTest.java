package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository repository;
    private static Sql2o db;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        db = configuration.databaseClient(datasource);

        repository = new Sql2oUserRepository(db);
    }

    @BeforeEach
    public void setUp() throws Exception {
        try (var connection = db.open()) {
            connection.createQuery("delete from users").executeUpdate();
        }
    }

    @Test
    public void whenTestIsSuccess() {
        var result = repository.save(new User(0, "hardsheller@gmail.com", "hardsheller", "123456"));
        assertThat(result).isPresent();
    }

    @Test
    public void whenCanFindByEmailAndPassword() {
        repository.save(new User(0, "hardsheller@gmail.com", "hardsheller", "123456")).get();
        var expected = repository.save(new User(0, "lostway@gmail.com", "asdda", "fdf")).get();
        repository.save(new User(0, "beginner@gmail.com", "dsad", "fdfdf")).get();
        var result = repository.findByEmailAndPassword(expected.getEmail(), expected.getPassword()).get();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenTestIsSuccessAndCanFindUser() {
        var expected = repository.save(new User(0, "hardsheller@gmail.com", "hardsheller", "123456")).get();
        var result = repository.findByEmailAndPassword(expected.getEmail(), expected.getPassword()).get();
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void whenMailTheSameThanThrowsException() {
        var user1 = new User(0, "hardsheller@gmail.com", "hardsheller", "123456");
        var user2 = new User(0, "hardsheller@gmail.com", "123", "123");
        repository.save(user1);
        assertThatThrownBy(() -> repository.save(user2)).isInstanceOf(Sql2oException.class);
    }
}