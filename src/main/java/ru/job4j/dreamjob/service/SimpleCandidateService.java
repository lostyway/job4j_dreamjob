package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService extends AbstractFileEntityService<Candidate> implements CandidateService  {
    private final CandidateRepository candidateRepository;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        super(fileService);
        this.candidateRepository = sql2oCandidateRepository;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        attachNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        if (isNewFileEmpty(image)) {
            return candidateRepository.update(candidate);
        }
        int oldFileId = candidate.getFileId();
        attachNewFile(candidate, image);
        boolean isUpdated = candidateRepository.update(candidate);
        deleteOldFile(oldFileId);
        return isUpdated;
    }

    @Override
    public boolean deleteById(int id) {
        return deleteEntityWithFileById(id, candidateRepository::findById, candidateRepository::deleteById);
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
