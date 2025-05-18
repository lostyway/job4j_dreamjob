package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryVacancyRepository implements VacancyRepository {
    private final Map<Integer, Vacancy> vacancies = new HashMap<>();
    private int nextId = 1;

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Just simple intern"));
        save(new Vacancy(0, "Junior Java Developer", "Just simple Jun"));
        save(new Vacancy(0, "Junior+ Java Developer", "Not a simple Jun"));
        save(new Vacancy(0, "Middle Java Developer", "Just simple middle"));
        save(new Vacancy(0, "Middle+ Java Developer", "not a simple middle"));
        save(new Vacancy(0, "Senior Java Developer", "boss senior"));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (key, oldVal)
                        -> new Vacancy(oldVal.getId(), vacancy.getTitle(), vacancy.getDescription())) != null;
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
