package nechto.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class RequestGameDto {
    @NotNull(message = "Date should not be null")
    private Date date;

    @NotNull(message = "User ids should not be null")
    private List<Long> userIds;
}
