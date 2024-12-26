package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.entity.Book;

public interface BookRepository extends MongoRepository<Book, String> {
}