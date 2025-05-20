package nechto.dto.request;

import lombok.Data;
import nechto.enums.Status;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RequestScoresDto {
    @NotNull(message = "User id should not be null")
    private Long userId;

    @NotNull(message = "Game id should not be null")
    private Long gameId;

    @NotNull(message = "Statuses should not be null")
    private List<Status> statuses;
}
