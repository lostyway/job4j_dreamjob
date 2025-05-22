package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.dreamjob.utility.HttpSessionChecker;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@Controller
@SuppressWarnings("unused")
public class IndexController {

    @GetMapping({"/", "/index"})
    public String getIndex(Model model, HttpSession session) {
        HttpSessionChecker.checkSession(session, model);
        return "index";
    }
}
