package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.dreamjob.utility.HttpSessionChecker;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@RestController
public class CountController {
    private final AtomicInteger total = new AtomicInteger(0);

    @GetMapping("/count")
    public String count(Model model, HttpSession session) {
        HttpSessionChecker.checkSession(session, model);
        int value = total.incrementAndGet();
        return String.format("Total execute : %d", value);
    }
}
