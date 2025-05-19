package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.File;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryFileRepository implements FileRepository {
    private final Map<Integer, File> files = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(0);

    @Override
    public File save(File file) {
        file.setId(nextId.incrementAndGet());
        files.put(file.getId(), file);
        return file;
    }

    @Override
    public Optional<File> findById(int id) {
        return Optional.ofNullable(files.get(id));
    }

    @Override
    public void deleteById(int id) {
        files.remove(id);
    }
}
