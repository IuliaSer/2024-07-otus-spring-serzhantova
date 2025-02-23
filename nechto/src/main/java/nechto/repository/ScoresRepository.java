package nechto.repository;

import nechto.entity.Scores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoresRepository extends JpaRepository<Scores, Long> {

    List<Scores> findAllByGameId(Long gameId);

    Optional<Scores> findByUserIdAndGameId(Long userId, Long gameId);
}
