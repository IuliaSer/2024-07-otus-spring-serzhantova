package nechto.dto.response;

import lombok.Data;
import nechto.enums.Status;

import java.util.List;

@Data
public class ResponseScoresDto {
    private Long id;

    private ResponseUserDto user;

    private ResponseGameDto game;

    private float scores;

    private List<Status> statuses;
}
