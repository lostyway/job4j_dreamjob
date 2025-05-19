package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Just simple intern", true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer", "Just simple Jun", true, 1, 0));
        save(new Vacancy(0, "Junior+ Java Developer", "Not a simple Jun", true, 2, 0));
        save(new Vacancy(0, "Middle Java Developer", "Just simple middle", false, 2, 0));
        save(new Vacancy(0, "Middle+ Java Developer", "not a simple middle", true, 3, 0));
        save(new Vacancy(0, "Senior Java Developer", "boss senior", true, 0, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (key, oldVal) ->
                new Vacancy(
                        oldVal.getId(), vacancy.getTitle(), vacancy.getDescription(),
                        vacancy.getVisible(), vacancy.getCityId(), vacancy.getFileId())
        ) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
