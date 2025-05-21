package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oCandidateRepositoryTest {
    private static Sql2oCandidateRepository candidateRepository;
    private static Sql2oFileRepository fileRepository;
    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        candidateRepository = new Sql2oCandidateRepository(sql2o);
        fileRepository = new Sql2oFileRepository(sql2o);
        file = new File("testCandidate", "testPathCandidate");
        fileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        fileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void deleteCandidate() {
        var candidates = candidateRepository.findAll();
        for (var candidate : candidates) {
            candidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    public void whenSaveCandidateThenSame() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = candidateRepository.save(new Candidate(0, "name", "description", creationDate, 1, file.getId()));
        var result = candidateRepository.findById(candidate.getId()).get();
        assertThat(result).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenDontSaveCandidateThenEmpty() {
        assertThat(candidateRepository.findById(0)).isEqualTo(empty());
        assertThat(candidateRepository.findAll()).isEqualTo(emptyList());
    }

    @Test
    public void whenFindAllCandidatesThenSame() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        var candidate2 = candidateRepository.save(new Candidate(0, "name2", "description2", creationDate, 2, file.getId()));
        var candidate3 = candidateRepository.save(new Candidate(0, "name3", "description3", creationDate, 1, file.getId()));
        var candidates = candidateRepository.findAll();
        assertThat(candidates).usingRecursiveComparison().isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    public void whenDeleteCandidateThenTrue() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        boolean result = candidateRepository.deleteById(candidate1.getId());
        assertThat(result).isTrue();
    }

    @Test
    public void whenDontDeleteCandidateThenFalse() {
        boolean result = candidateRepository.deleteById(0);
        assertThat(result).isFalse();
    }

    @Test
    public void whenUpdateCandidateThenTrue() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        var candidateToUpdate = new Candidate(candidate1.getId(), "name2", "description2", creationDate, 2, file.getId());
        boolean result = candidateRepository.update(candidateToUpdate);
        var candidateResult = candidateRepository.findById(candidateToUpdate.getId()).get();
        assertThat(result).isTrue();
        assertThat(candidate1).usingRecursiveComparison().isNotEqualTo(candidateToUpdate);
        assertThat(candidateResult).usingRecursiveComparison().isEqualTo(candidateToUpdate);
    }

    @Test
    public void whenUpdateCandidateThenResultEqualsToNewCandidate() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        var candidateToUpdate = new Candidate(candidate1.getId(), "name2", "description2", creationDate, 2, file.getId());
        candidateRepository.update(candidateToUpdate);
        var candidateResult = candidateRepository.findById(candidateToUpdate.getId()).get();
        assertThat(candidateResult).usingRecursiveComparison().isEqualTo(candidateToUpdate);
    }

    @Test
    public void whenUpdateCandidateThatNotEqualsWithOld() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        var candidateToUpdate = new Candidate(candidate1.getId(), "name2", "description2", creationDate, 2, file.getId());
        candidateRepository.update(candidateToUpdate);
        var candidateResult = candidateRepository.findById(candidateToUpdate.getId()).get();
        assertThat(candidate1).usingRecursiveComparison().isNotEqualTo(candidateResult);
    }

    @Test
    public void whenUpdateCandidateThenFalse() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        candidateRepository.save(new Candidate(0, "name1", "description1", creationDate, 1, file.getId()));
        boolean result = candidateRepository.update(new Candidate(3, "name1", "description1", creationDate, 1, file.getId()));
        assertThat(result).isFalse();
    }
}