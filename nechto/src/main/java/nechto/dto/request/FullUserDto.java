package nechto.dto.request;

import lombok.Data;
import nechto.enums.Role;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FullUserDto {
    @NotNull(message = "Id should not be null")
    private Long id;

    @Size(min = 2, max = 20, message = "Name should have expected size")
    private String name;

    @NotNull(message = "Username should not be null")
    @Size(min = 2, max = 20, message = "Username should have expected size")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 2, max = 20, message = "Password should have expected size")
    private String password;

    private Role role;
}

