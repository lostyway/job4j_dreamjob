package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

import java.util.Collection;
import java.util.Optional;

public class SimpleCandidateService implements CandidateService {
    private final CandidateRepository candidateRepository = MemoryCandidateRepository.getInstance();
    private static final CandidateService INSTANCE = new SimpleCandidateService();

    private SimpleCandidateService() {
    }

    public static CandidateService getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
       return candidateRepository.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return candidateRepository.deleteById(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidateRepository.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
