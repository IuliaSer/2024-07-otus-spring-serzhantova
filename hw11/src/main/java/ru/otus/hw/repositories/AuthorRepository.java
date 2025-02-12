package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Author;

@Repository
public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

}