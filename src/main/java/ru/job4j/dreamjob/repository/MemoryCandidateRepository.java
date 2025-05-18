package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Yura", "3 years of experience", 6));
        save(new Candidate(0, "Vasya", "5 years of experience", 2));
        save(new Candidate(0, "Masha", "2 years of experience in HR", 3));
        save(new Candidate(0, "Lera", "1 years of experience in Yandex", 1));
        save(new Candidate(0, "Misha", "4 years of experience in street food", 2));
        save(new Candidate(0, "Valera", "0 years of experience", 2));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (key, oldValue) ->
                new Candidate(
                        candidate.getId(), candidate.getName(), candidate.getDescription(),
                        candidate.getCreationDate(), candidate.getCityId())
        ) != null;
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
