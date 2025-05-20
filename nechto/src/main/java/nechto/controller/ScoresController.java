package nechto.controller;

import lombok.RequiredArgsConstructor;
import nechto.dto.request.RequestScoresDto;
import nechto.dto.response.ResponseScoresDto;
import nechto.service.ScoresService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoresController {

    private final ScoresService scoresService;

    @PatchMapping
    ResponseScoresDto updateScores(@Valid @RequestBody RequestScoresDto scores) {
        return scoresService.updateScores(scores);
    }

    @PatchMapping("/{game_id}")
    List<ResponseScoresDto> countForAll(@PathVariable(name = "game_id") Long gameId) {
        return scoresService.countForAll(gameId);
    }
}
