package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VacancyControllerTest {
    private VacancyService vacancyService;
    private CityService cityService;
    private VacancyController vacancyController;
    private MultipartFile testFile;

    @BeforeEach
    public void initService() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("textFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCity = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCity);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualCity = model.getAttribute("cities");
        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualCity).isEqualTo(expectedCity);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws IOException {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancies = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancies).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenUpdateIsSuccessfulThenRedirectToVacancies() throws IOException {
        var vacancyToUpdate = new Vacancy(1, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancyToUpdate, testFile, model);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenUpdateIsFailedThenTError404() throws IOException {
        var vacancyToUpdate = new Vacancy();
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenReturn(false);
        var model = new ConcurrentModel();

        var view = vacancyController.update(vacancyToUpdate, testFile, model);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenDeleteIsSuccessfulThenRedirectToVacancies() {
        var vacancyToDelete = new Vacancy(1, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.deleteById(vacancyToDelete.getId())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, vacancyToDelete.getId());

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenDeleteIsFailedThenRedirectToErrors404AndGetMessage() {
        var vacancyToDelete = new Vacancy(1, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.deleteById(vacancyToDelete.getId())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, vacancyToDelete.getId());

        assertThat(model.getAttribute("message")).isEqualTo("Вакансия с указанным идентификатором не найдена");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenGettingVacancyByIdThenRedirectToVacanciesPage() {
        var city = new City(3, "Москва");
        var vacancy = new Vacancy(1, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        when(cityService.findAll()).thenReturn(List.of(city));

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, vacancy.getId());

        assertThat(model.getAttribute("vacancy")).isEqualTo(vacancy);
        assertThat(model.getAttribute("cities")).isEqualTo(List.of(city));
        assertThat(view).isEqualTo("vacancies/one");
    }

    @Test
    public void whenNotGettingVacancyByIdThenRedirectToError404AndGetMessage() {
        var vacancy = new Vacancy(1, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.findById(vacancy.getId())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, vacancy.getId());

        assertThat(model.getAttribute("message")).isEqualTo("Вакансия с указанным идентификатором не найдена");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdatingAndGetException() throws IOException {
        var exception = new RuntimeException("Failed to write file");
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenThrow(exception);

        var model = new ConcurrentModel();
        var exceptionResult = vacancyController.update(new Vacancy(), testFile, model);

        assertThat(model.getAttribute("message")).isEqualTo("Произошла ошибка при сохранении вакансии." + exception.getMessage());
        assertThat(exceptionResult).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdatingAndGetIOExceptionThenReturnsErrorViewAndMessage() throws IOException {
        doThrow(new IOException("Failed to write file")).when(vacancyService).update(any(Vacancy.class), any(FileDto.class));

        var model = new ConcurrentModel();
        var viewName = vacancyController.update(new Vacancy(), testFile, model);

        assertThat(model.getAttribute("message")).isEqualTo("Проблемы с чтением файла");
        assertThat(viewName).isEqualTo("errors/404");
    }
}