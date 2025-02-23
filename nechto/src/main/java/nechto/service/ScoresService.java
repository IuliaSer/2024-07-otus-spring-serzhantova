package nechto.service;

import nechto.dto.response.ResponseScoresDto;
import nechto.dto.request.RequestScoresDto;

import java.util.List;

public interface ScoresService {
    List<ResponseScoresDto> countForAll(Long gameId);

    ResponseScoresDto updateScores(RequestScoresDto scores);
}
