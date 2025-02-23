package nechto.service;

import nechto.dto.response.ResponseGameDto;
import nechto.dto.request.RequestGameDto;

import java.util.List;

public interface GameService {
    ResponseGameDto save(RequestGameDto game);

    ResponseGameDto addUser(Long gameId, Long userId);

    void deleteUserFromGame(Long gameId, Long userId);

    List<ResponseGameDto> findAll();

    void deleteGame(Long gameId);
}
