package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserService userService;
    private HttpServletRequest request = new MockHttpServletRequest();
    private HttpSession session = new MockHttpSession();

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegistrationPageThenReturnRegistrationView() {
        var registrationPage = userController.getRegistrationPage();
        assertThat(registrationPage).isEqualTo("users/register");
    }

    @Test
    public void whenRegistrationIsSuccessfulThenGetLoginPage() {
        var userToSave = new User(1, "qwerty@gmail.com", "qwerty", "password");
        when(userService.save(userToSave)).thenReturn(Optional.of(userToSave));

        var model = new ConcurrentModel();
        var view = userController.register(userToSave, model);
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenRegistrationIsFailedThenGetErrorPageAndMessage() {
        var user = new User(0, "qwerty@gmail.com", "qwerty", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message"))
                .isEqualTo(String.format("Пользователь с почтой %s уже существует", user.getEmail()));
    }

    @Test
    public void whenGettingLoginPageThenGetLoginPage() {
        var loginPage = userController.getLoginPage();
        assertThat(loginPage).isEqualTo("users/login");
    }

    @Test
    public void whenLoginInSuccessfulThenGetVacanciesPage() {
        var user = new User();
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.login(user, model, request);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginIsSuccessfulThenUserIsStoredInSession() {
        var user = new User(1, "mail@mail.ru", "name", "pass");
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var mockRequest = new MockHttpServletRequest();
        userController.login(user, model, mockRequest);

        var sessionUser = mockRequest.getSession().getAttribute("user");
        assertThat(sessionUser).isEqualTo(user);
    }

    @Test
    public void whenLoginInFailedThenGetLoginPage() {
        var user = new User();
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.login(user, model, request);

        assertThat(view).isEqualTo("users/login");
        assertThat(model.getAttribute("error")).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLogoutThenGetLoginPage() {
        var view = userController.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}