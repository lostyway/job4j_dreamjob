package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.HasFileId;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class AbstractFileEntityService<T extends HasFileId> {
    protected final FileService fileService;

    protected AbstractFileEntityService(FileService fileService) {
        this.fileService = fileService;
    }

    protected boolean isNewFileEmpty(FileDto image) {
        return image == null || image.isEmpty();
    }

    protected void attachNewFile(T entity, FileDto image) {
        var file = fileService.save(image);
        entity.setFileId(file.getId());
    }

    protected void deleteOldFile(int fileId) {
        fileService.deleteById(fileId);
    }

    protected boolean deleteEntityWithFileById(int id, IntFunction<Optional<T>> findById, IntPredicate deleteById) {
        var entityOpt = findById.apply(id);
        if (entityOpt.isEmpty()) {
            return false;
        }

        boolean deletedFromMemory = deleteById.test(id);
        if (deletedFromMemory) {
            deleteOldFile(entityOpt.get().getFileId());
            return true;
        }
        return false;
    }
}
