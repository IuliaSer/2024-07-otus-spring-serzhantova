package nechto.repository;

import nechto.entity.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @EntityGraph(value = "game_entity_graph")
    List<Game> findAll();
}
