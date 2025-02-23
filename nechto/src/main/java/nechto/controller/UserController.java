package nechto.controller;

import lombok.RequiredArgsConstructor;
import nechto.dto.request.FullUserDto;
import nechto.dto.request.RequestUserDto;
import nechto.dto.response.ResponseUserDto;
import nechto.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    List<ResponseUserDto> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/ordered")
    List<ResponseUserDto> getUsersOrderedByGamesAmount() {
        return userService.findUsersOrderedByGamesAmount();
    }

    @GetMapping("/username")
    ResponseUserDto findUserByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @PostMapping
    void saveUser(@Valid @RequestBody RequestUserDto user) {
        userService.saveUser(user);
    }

    @PatchMapping("/admin/{user_id}")
    void makeAdmin(@PathVariable(name = "user_id") Long userId) {
        userService.makeAdmin(userId);
    }

    @PutMapping
    ResponseUserDto updateUser(@Valid @RequestBody FullUserDto user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{user_id}")
    void deleteUser(@PathVariable(name = "user_id") Long userId) {
        userService.deleteUser(userId);
    }
}
