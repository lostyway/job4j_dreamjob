package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileControllerTest {
    private FileController fileController;
    private FileService fileService;

    @BeforeEach
    public void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenGetByIdSuccessfulThan200() {
        var expectedStatusCode = HttpStatus.OK;
        var expectedContent = new byte[] {1, 2, 3};
        when(fileService.getFileById(1)).thenReturn(Optional.of(new FileDto("fileName", expectedContent)));

        var actualResponse = fileController.getById(1);
        var actualStatusCode = actualResponse.getStatusCode();
        var actualContent = actualResponse.getBody();

        assertEquals(expectedStatusCode, actualStatusCode);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void whenGetByIdFailedThanGet404() {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());
        var actualResponse = fileController.getById(1);
        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
    }
}