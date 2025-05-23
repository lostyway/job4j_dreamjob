package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexControllerTest {

    @Test
    public void getIndex() {
        IndexController controller = new IndexController();
        var view = controller.getIndex();
        assertThat(view).isEqualTo("index");
    }
}