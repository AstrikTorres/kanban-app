package com.astrik.kanban.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astrik.kanban.entity.task.Task;
import com.astrik.kanban.entity.user.User;
import com.astrik.kanban.repository.ToDoRepository;
import com.astrik.kanban.repository.UserRepository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class TaskService {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private UserService userService;

    public List<Task> reedTodosByUser() {
        return toDoRepository.findByUserId(userService.getAuthUser().getId());
    }

    public List<Task> setTodosDefault() {
        User user = userService.getAuthUser();
        Task toDo1 = new Task("Cortar cebolla", false, user);
        Task toDo2 = new Task("Llorar con la llorona", true, user);
        Task toDo3 = new Task("Hacer el curso de Spring Boot", true, user);
        Task toDo4 = new Task("Hacer el curso de React", true, user);

        List<Task> toDos = Arrays.asList(toDo1, toDo2, toDo3, toDo4);

        toDoRepository.saveAll(toDos);
        return toDos;
    }

    public List<Task> setTodosDefault(User user) {
        Task toDo1 = new Task("Cortar cebolla", false, user);
        Task toDo2 = new Task("Llorar con la llorona", true, user);
        Task toDo3 = new Task("Hacer el curso de Spring Boot", true, user);
        Task toDo4 = new Task("Hacer el curso de React", true, user);

        List<Task> toDos = Arrays.asList(toDo1, toDo2, toDo3, toDo4);

        toDoRepository.saveAll(toDos);
        return toDos;
    }

    public Task saveTodo(Task toDo) {
        toDo.setUser(userService.getAuthUser());
        return toDoRepository.save(toDo);
    }

    public List<Task> saveTodoList(List<Task> toDos) {
        User user = userService.getAuthUser();
        List<Task> toDosSaved = new ArrayList<>();
        toDos.forEach(toDo -> {
            toDo.setUser(user);
            toDoRepository.save(toDo);
            toDosSaved.add(toDo);
        });
        return toDosSaved;
    }

    public Task updateTodo(Task toDo) {
        User user = userService.getAuthUser();
        return toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((item) -> {
                    toDo.setUser(user);
                    return toDoRepository.save(toDo);
                }).orElseThrow(() -> new RuntimeException("id not found"));
    }

    public List<Task> updateTodoList(List<Task> toDos) throws RuntimeException {
        User user = userService.getAuthUser();
        List<Task> toDosUpdated = new ArrayList<>();
        toDos.forEach(toDo -> toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((item) -> {
                    toDo.setUser(user);
                    return toDosUpdated.add(toDoRepository.save(toDo));
                }).orElseThrow(() -> new RuntimeException("todo id not found: " + toDo.getId()))
        );
        return toDosUpdated;
    }

    public boolean removeTodo(Long id) {
        User user = userService.getAuthUser();
        if (!toDoRepository.existsByIdAndUserId(id, user.getId())) return false;
        try {
            toDoRepository.deleteByIdAndUserId(id, user.getId());
            return !toDoRepository.existsByIdAndUserId(id, user.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeTodoList(List<Task> toDos) {
        User user = userService.getAuthUser();
        toDos.forEach(toDo -> toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((t) -> {
                    toDoRepository.deleteByIdAndUserId(toDo.getId(), user.getId());
                    return true;
                }).orElseThrow(() -> new RuntimeException("id not found " + toDo.getId()))
        );
        return true;
    }
}
