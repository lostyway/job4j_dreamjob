package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private final Map<Integer, Candidate> candidates = new HashMap<>();
    private int nextId = 1;

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Yura", "3 years of experience"));
        save(new Candidate(0, "Vasya", "5 years of experience"));
        save(new Candidate(0, "Masha", "2 years of experience in HR"));
        save(new Candidate(0, "Lera", "1 years of experience in Yandex"));
        save(new Candidate(0, "Misha", "4 years of experience in street food"));
        save(new Candidate(0, "Valera", "0 years of experience"));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (key, oldValue) ->
                        new Candidate(oldValue.getId(),
                                candidate.getName(),
                                candidate.getDescription(),
                                candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
