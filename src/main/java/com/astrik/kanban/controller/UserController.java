package com.astrik.kanban.controller;

import com.astrik.kanban.entity.User;
import com.astrik.kanban.entity.UserValid;
import com.astrik.kanban.service.UserDetailsServiceImpl;
import com.astrik.kanban.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private UserService userService;
    private ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.reedUsers(), HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<User> getAuthUser() {
        return new ResponseEntity<>(UserDetailsServiceImpl.getAuthUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody @Validated(UserValid.class) User user) {
        User userCreated = userService.saveUser(user);
        return userCreated.getUsername() != user.getUsername()
                ? new ResponseEntity<>(userCreated, HttpStatus.SEE_OTHER)
                : new ResponseEntity<>(userCreated, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Object> putUser(
            @RequestBody @Validated(UserValid.class) User user) {
        User userUpdated = userService.updateUser(user);
        return userUpdated.getUsername() != user.getUsername()
                ? new ResponseEntity<>(userUpdated, HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @DeleteMapping
    ResponseEntity<ObjectNode> deleteUser() {
        String[] msg = {
                "Deleted User",
                "Try again - verify param"
        };

        ObjectNode objectNode = objectMapper.createObjectNode();

        return userService.removeUser()
                ? ResponseEntity.status(HttpStatus.OK).body(objectNode.put("message", msg[0]))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode.put("message", msg[1]));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ObjectNode> handleConstraintViolationException(ConstraintViolationException e) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode.put("message", e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ObjectNode handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        ObjectNode errors = objectMapper.createObjectNode();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                                errors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                ));
        return errors;
    }
}
