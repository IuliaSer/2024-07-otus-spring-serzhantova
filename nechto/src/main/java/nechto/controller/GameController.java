package nechto.controller;

import lombok.RequiredArgsConstructor;
import nechto.dto.request.RequestGameDto;
import nechto.dto.response.ResponseGameDto;
import nechto.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping
    List<ResponseGameDto> getGames() {
        return gameService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ResponseGameDto save(@Valid @RequestBody RequestGameDto game) {
        return gameService.save(game);
    }

    @PatchMapping("/{game_id}/user/{user_id}")
    ResponseGameDto addUserToGame(@PathVariable(name = "game_id") Long gameId,
                                  @PathVariable(name = "user_id") Long userId) {
        return gameService.addUser(gameId, userId);
    }

    @DeleteMapping("/{game_id}/user/{user_id}")
    void deleteUserFromGame(@PathVariable(name = "game_id") Long gameId,
                            @PathVariable(name = "user_id") Long userId) {
        gameService.deleteUserFromGame(gameId, userId);
    }

    @DeleteMapping("/{game_id}")
    void deleteGame(@PathVariable(name = "game_id") Long gameId) {
        gameService.deleteGame(gameId);
    }
}
