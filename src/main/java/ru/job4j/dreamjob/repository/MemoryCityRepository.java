package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
@Repository
public class MemoryCityRepository implements CityRepository {
   private final Map<Integer, City> cities = new ConcurrentHashMap<>();

    public MemoryCityRepository() {
        cities.put(1, new City(1, "Ахтубинск"));
        cities.put(2, new City(2, "Уральск"));
        cities.put(3, new City(3, "Москва"));
        cities.put(4, new City(4, "Астрахань"));
        cities.put(5, new City(5, "Санкт-Петербург"));
        cities.put(6, new City(6, "Краснодар"));
    }

    @Override
    public Collection<City> findAll() {
        return cities.values();
    }
}
