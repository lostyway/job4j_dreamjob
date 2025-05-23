package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CountControllerTest {
    private CountController controller;

    @BeforeEach
    public void setUp() {
        controller = new CountController();
    }

    @Test
    public void whenOneCount() {
        var view = controller.count();
        assertThat(view).isEqualTo("Total execute : 1");
    }

    @Test
    public void whenTwoCount() {
        controller.count();
        var view = controller.count();
        assertThat(view).isEqualTo("Total execute : 2");
    }

    @Test
    public void whenTenCount() {
        for (int i = 0; i < 9; i++) {
            controller.count();
        }

        var view = controller.count();
        assertThat(view).isEqualTo("Total execute : 10");
    }
}