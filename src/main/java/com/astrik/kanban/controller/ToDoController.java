package com.astrik.kanban.controller;

import com.astrik.kanban.entity.*;
import com.astrik.kanban.service.ToDoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Validated
public class ToDoController {

    private ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping("/default")
    public ResponseEntity<List<ToDo>> createDefaultTodos() {
        return new ResponseEntity<>(toDoService.setTodosDefault(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ToDo>> getAllTodosByUserId() {
        return new ResponseEntity<>(toDoService.reedTodosByUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ToDo> createTodo(
            @RequestBody @Validated(ToDoCreate.class) ToDo toDo) {
        return new ResponseEntity<>(toDoService.saveTodo(toDo), HttpStatus.CREATED);
    }

    @PostMapping("/list")
    public ResponseEntity<List<ToDo>> createTodoList(
            @RequestBody List<ToDo> todosList) {
        return new ResponseEntity<>(toDoService.saveTodoList(todosList), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ToDo> putTodo(
            @RequestBody @Validated(ToDoUpdate.class) ToDo toDo) {
        return new ResponseEntity<>(toDoService.updateTodo(toDo), HttpStatus.OK);
    }

    @PutMapping("/list")
    public ResponseEntity<List<ToDo>> putTodoList(
            @RequestBody List<ToDo> toDoList) {
        return new ResponseEntity<>(toDoService.updateTodoList(toDoList), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ObjectNode> deleteTodo(@PathVariable @Min(1) Long id) {
        String[] msg = {
                "Deleted to do",
                "Try again - verify param"
        };

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        return toDoService.removeTodo(id)
                ? ResponseEntity.status(HttpStatus.OK).body(objectNode.put("message", msg[0]))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode.put("message", msg[1]));
    }

    @DeleteMapping(path = "/list")
    ResponseEntity<ObjectNode> deleteTodoList(@RequestBody List<ToDo> toDoList) {
        String[] msg = {
                "Deleted to do's",
                "Try again - verify param"
        };

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        return toDoService.removeTodoList(toDoList)
                ? ResponseEntity.status(HttpStatus.OK).body(objectNode.put("message", msg[0]))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode.put("message", msg[1]));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ObjectNode> handleConstraintViolationException(ConstraintViolationException e) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode.put("message", e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ObjectNode handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errors = mapper.createObjectNode();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error ->
                                errors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                ));

        return errors;
    }

}
