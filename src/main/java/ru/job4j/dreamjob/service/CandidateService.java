package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface CandidateService {
    Candidate save(Candidate candidate, FileDto file);

    boolean deleteById(int id);

    boolean update(Candidate candidate, FileDto file) throws IOException;

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
