package nechto.mappers;

import nechto.dto.response.ResponseGameDto;
import nechto.entity.Game;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {
    ResponseGameDto convertToResponseGameDto(Game game);

    Game convertToGame(ResponseGameDto game);

    List<ResponseGameDto> convertToListResponseGameDto(List<Game> games);
}
