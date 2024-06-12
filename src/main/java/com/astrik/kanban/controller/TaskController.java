package com.astrik.kanban.controller;

import com.astrik.kanban.entity.task.CreateTask;
import com.astrik.kanban.entity.task.Task;
import com.astrik.kanban.entity.task.UpdateTask;
import com.astrik.kanban.service.TaskService;
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
public class TaskController {

    private TaskService toDoService;

    public TaskController(TaskService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping("/default")
    public ResponseEntity<List<Task>> createDefaultTodos() {
        return new ResponseEntity<>(toDoService.setTodosDefault(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTodosByUserId() {
        return new ResponseEntity<>(toDoService.reedTodosByUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Task> createTodo(
            @RequestBody @Validated(CreateTask.class) Task toDo) {
        return new ResponseEntity<>(toDoService.saveTodo(toDo), HttpStatus.CREATED);
    }

    @PostMapping("/list")
    public ResponseEntity<List<Task>> createTodoList(
            @RequestBody List<Task> todosList) {
        return new ResponseEntity<>(toDoService.saveTodoList(todosList), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Task> putTodo(
            @RequestBody @Validated(UpdateTask.class) Task toDo) {
        return new ResponseEntity<>(toDoService.updateTodo(toDo), HttpStatus.OK);
    }

    @PutMapping("/list")
    public ResponseEntity<List<Task>> putTodoList(
            @RequestBody List<Task> toDoList) {
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
    ResponseEntity<ObjectNode> deleteTodoList(@RequestBody List<Task> toDoList) {
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
