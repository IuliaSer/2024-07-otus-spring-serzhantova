package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String username);
}
