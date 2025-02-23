package nechto.mappers;

import nechto.dto.request.RequestUserDto;
import nechto.dto.response.ResponseUserDto;
import nechto.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ResponseUserDto convertToResponseUserDto(User user);

    User convertToUser(RequestUserDto userDto);

    List<ResponseUserDto> convertToListOfResponseUserDto(List<User> users);

}
