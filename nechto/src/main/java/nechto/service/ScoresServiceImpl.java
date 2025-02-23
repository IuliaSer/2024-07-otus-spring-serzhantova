package nechto.service;

import lombok.RequiredArgsConstructor;
import nechto.dto.request.RequestScoresDto;
import nechto.dto.response.ResponseScoresDto;
import nechto.entity.Scores;
import nechto.enums.Status;
import nechto.mappers.ScoresMapper;
import nechto.repository.ScoresRepository;
import nechto.status.StatusProcessorImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoresServiceImpl implements ScoresService {
    private final ScoresRepository scoresRepository;

    private final ScoresMapper scoresMapper;

    private final StatusProcessorImpl statusProcessorImpl;

    @Override
    public ResponseScoresDto updateScores(RequestScoresDto scoresDto) {
        Long userId = scoresDto.getUserId();
        Long gameId = scoresDto.getGameId();
        Scores scores = scoresRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Scores with user id %s and game id %s not found", userId, gameId)));
        scores.setStatuses(scoresDto.getStatuses());
        return scoresMapper.convertToResponseScoresDto(scoresRepository.save(scores));
    }

    @Override
    public List<ResponseScoresDto> countForAll(Long gameId) {
        List<Scores> scoresList = scoresRepository.findAllByGameId(gameId);

        for (Scores scores : scoresList) {
            List<Status> statuses = scores.getStatuses();
            float results = 0;

            for (Status status : statuses) {
                results += statusProcessorImpl.processStatus(statuses, scoresList, status);
            }
            scores.setScores(results);
            scoresRepository.save(scores);
        }
        return scoresMapper.convertToListResponseScoresDto(scoresList);
    }
}
