package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CandidateControllerTest {
    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void initService() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("textFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var candidate2 = new Candidate(2, "test2", "desc2", now(), 3, 4);
        var expectedVacancies = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(List.of(candidate1, candidate2));

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualVacancies = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCity = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCity);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCity = model.getAttribute("cities");
        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCity).isEqualTo(expectedCity);
    }

    @Test
    public void whenUpdateIsSuccessfulThenRedirectToCandidates() throws IOException {
        var candidateToUpdate = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(true);
        var model = new ConcurrentModel();

        var view = candidateController.update(candidateToUpdate, testFile, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenUpdateIsFailedThenTError404() throws IOException {
        var candidateToUpdate = new Candidate();
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(false);
        var model = new ConcurrentModel();

        var view = candidateController.update(candidateToUpdate, testFile, model);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenDeleteIsSuccessfulThenRedirectToCandidates() {
        var candidateToDelete = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.deleteById(candidateToDelete.getId())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, candidateToDelete.getId());

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteIsFailedThenRedirectToErrors404AndGetMessage() {
        var vacancyToDelete = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.deleteById(vacancyToDelete.getId())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, vacancyToDelete.getId());

        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с таким id не был найден");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenGettingCandidateByIdThenRedirectToCandidatesPage() {
        var city = new City(3, "Москва");
        var candidate = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(cityService.findAll()).thenReturn(List.of(city));

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, candidate.getId());

        assertThat(model.getAttribute("candidate")).isEqualTo(candidate);
        assertThat(model.getAttribute("cities")).isEqualTo(List.of(city));
        assertThat(view).isEqualTo("candidates/one");
    }

    @Test
    public void whenNotGettingCandidateByIdThenRedirectToError404AndGetMessage() {
        var candidate = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.findById(candidate.getId())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, candidate.getId());

        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с таким id не был найден");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdatingAndGetIOExceptionThenReturnsErrorViewAndMessage() throws IOException {
        doThrow(new IOException("Failed to write file")).when(candidateService).update(any(Candidate.class), any(FileDto.class));

        var model = new ConcurrentModel();
        var viewName = candidateController.update(new Candidate(), testFile, model);

        assertThat(model.getAttribute("message")).isEqualTo("Произошла проблема при чтении файла");
        assertThat(viewName).isEqualTo("errors/404");
    }

    @Test
    public void whenUpdatingAndGetRuntimeThenReturnsErrorViewAndMessage() throws IOException {
        doThrow(new RuntimeException("Failed")).when(candidateService).update(any(Candidate.class), any(FileDto.class));

        var model = new ConcurrentModel();
        var viewName = candidateController.update(new Candidate(), testFile, model);

        assertThat(model.getAttribute("message")).isEqualTo("Произошла ошибка при добавлении резюме");
        assertThat(viewName).isEqualTo("errors/404");
    }

    @Test
    public void whenCreateCandidateSuccessfullyThenRedirectToCandidatesPage() {
        var candidateToCreate = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.save(any(), any())).thenReturn(candidateToCreate);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidateToCreate, testFile, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenCreateCandidateFailedThenRedirectToErrorPageAndGetMessage() {
        var candidateToCreate = new Candidate(1, "test2", "desc2", now(), 3, 4);
        when(candidateService.save(any(), any())).thenReturn(null);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidateToCreate, testFile, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Произошла ошибка при создании вакансии");
    }
}