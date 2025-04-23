package br.com.diegobrandao.archunit.ArchUnit.controller;

import br.com.diegobrandao.archunit.ArchUnit.domain.User;
import br.com.diegobrandao.archunit.ArchUnit.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping("user/")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User createUser(String username, String email) {
        return userService.createUser(username, email);
    }

    public Optional<User> getUser(Long id) {
        return userService.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }
}