package nechto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import nechto.enums.Role;

@Data
@AllArgsConstructor
public class ResponseUserDto {
    private Long id;

    private String name;

    private String username;

    private Role role;
}

