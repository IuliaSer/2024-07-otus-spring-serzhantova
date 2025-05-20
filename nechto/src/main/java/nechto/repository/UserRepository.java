package nechto.repository;

import nechto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = """
        SELECT u.id, u.name, u.username, u.password, u.role
        FROM USERS u
        INNER JOIN SCORES s ON u.id = s.user_id
        GROUP BY u.id
        ORDER BY COUNT(u.id) DESC
    """, nativeQuery = true)
    List<User> findAllOrderedByGameAmount();
}
