package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleVacancyService extends AbstractFileEntityService<Vacancy> implements VacancyService {
    private final VacancyRepository vacancyRepository;

    public SimpleVacancyService(VacancyRepository sql2oVacancyRepository, FileService fileService) {
        super(fileService);
        this.vacancyRepository = sql2oVacancyRepository;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto image) {
        attachNewFile(vacancy, image);
        return vacancyRepository.save(vacancy);
    }

    @Override
    public boolean deleteById(int id) {
        return deleteEntityWithFileById(id, vacancyRepository::findById, vacancyRepository::deleteById);
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto image) {
        if (isNewFileEmpty(image)) {
            return vacancyRepository.update(vacancy);
        }
        int oldFileId = vacancy.getFileId();
        attachNewFile(vacancy, image);
        boolean isUpdated = vacancyRepository.update(vacancy);
        deleteOldFile(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}
