package nechto.mappers;

import nechto.dto.response.ResponseScoresDto;
import nechto.entity.Scores;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScoresMapper {

    ResponseScoresDto convertToResponseScoresDto(Scores scores);

    List<ResponseScoresDto> convertToListResponseScoresDto(List<Scores> scores);

}
