package me.dolia.pmm.web;

import me.dolia.pmm.user.User;
import me.dolia.pmm.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static me.dolia.pmm.web.UserController.USERS_API_URL;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@RequestMapping(USERS_API_URL)
public class UserController {

    static final String USERS_API_URL = "/users";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = POST)
    public ResponseEntity createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .path(savedUser.getId().toString())
                        .build()
                        .toUri()
        ).build();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public User readUser(@PathVariable long id) {
        return userService.findBy(id);
    }

    @RequestMapping(value = "/me", method = GET)
    public User readAuthenticatedUser(@AuthenticationPrincipal User user) {
        return user;
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteUser(@PathVariable long id) {
        User user = userService.findBy(id);
        userService.delete(user);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/available", method = GET)
    public boolean checkEmail(@RequestParam String email) {
        return userService.isAvailable(email);
    }
}