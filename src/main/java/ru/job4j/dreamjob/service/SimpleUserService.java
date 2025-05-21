package ru.job4j.dreamjob.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.UserRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleUserService implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleUserService.class);
    private final UserRepository userRepository;

    public SimpleUserService(UserRepository sql2oUserRepository) {
        userRepository = sql2oUserRepository;
    }

    @Override
    public Optional<User> save(User user) {
        try {
            return userRepository.save(user);
        } catch (Sql2oException e) {
            LOG.error("Пользователь с почтой: '{}' уже существует", user.getEmail(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
