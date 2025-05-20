package nechto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseGameDto {
    private Long id;

    private Date date;

    private List<ResponseUserDto> users;
}
