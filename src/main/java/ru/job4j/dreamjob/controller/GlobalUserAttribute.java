package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.job4j.dreamjob.model.User;

@ControllerAdvice
public class GlobalUserAttribute {

    @ModelAttribute("sessionUser")
    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
