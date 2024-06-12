package com.astrik.kanban.service;

import org.springframework.stereotype.Service;

import com.astrik.kanban.entity.ToDo;
import com.astrik.kanban.entity.User;
import com.astrik.kanban.repository.ToDoRepository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class ToDoService {

    ToDoRepository toDoRepository;
    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public List<ToDo> reedTodosByUser() {
        return toDoRepository.findByUserId(UserDetailsServiceImpl.getAuthUser().getId());
    }

    public List<ToDo> setTodosDefault() {
        User user = UserDetailsServiceImpl.getAuthUser();
        ToDo toDo1 = new ToDo("Cortar cebolla", false, user);
        ToDo toDo2 = new ToDo("Llorar con la llorona", true, user);
        ToDo toDo3 = new ToDo("Hacer el curso de Spring Boot", true, user);
        ToDo toDo4 = new ToDo("Hacer el curso de React", true, user);

        List<ToDo> toDos = Arrays.asList(toDo1, toDo2, toDo3, toDo4);

        toDoRepository.saveAll(toDos);
        return toDos;
    }

    public List<ToDo> setTodosDefault(User user) {
        ToDo toDo1 = new ToDo("Cortar cebolla", false, user);
        ToDo toDo2 = new ToDo("Llorar con la llorona", true, user);
        ToDo toDo3 = new ToDo("Hacer el curso de Spring Boot", true, user);
        ToDo toDo4 = new ToDo("Hacer el curso de React", true, user);

        List<ToDo> toDos = Arrays.asList(toDo1, toDo2, toDo3, toDo4);

        toDoRepository.saveAll(toDos);
        return toDos;
    }

    public ToDo saveTodo(ToDo toDo) {
        toDo.setUser(UserDetailsServiceImpl.getAuthUser());
        return toDoRepository.save(toDo);
    }

    public List<ToDo> saveTodoList(List<ToDo> toDos) {
        User user = UserDetailsServiceImpl.getAuthUser();
        List<ToDo> toDosSaved = new ArrayList<>();
        toDos.forEach(toDo -> {
            toDo.setUser(user);
            toDoRepository.save(toDo);
            toDosSaved.add(toDo);
        });
        return toDosSaved;
    }

    public ToDo updateTodo(ToDo toDo) {
        User user = UserDetailsServiceImpl.getAuthUser();
        return toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((item) -> {
                    toDo.setUser(user);
                    return toDoRepository.save(toDo);
                }).orElseThrow(() -> new RuntimeException("id not found"));
    }

    public List<ToDo> updateTodoList(List<ToDo> toDos) throws RuntimeException {
        User user = UserDetailsServiceImpl.getAuthUser();
        List<ToDo> toDosUpdated = new ArrayList<>();
        toDos.forEach(toDo -> toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((item) -> {
                    toDo.setUser(user);
                    return toDosUpdated.add(toDoRepository.save(toDo));
                }).orElseThrow(() -> new RuntimeException("todo id not found: " + toDo.getId()))
        );
        return toDosUpdated;
    }

    public boolean removeTodo(Long id) {
        User user = UserDetailsServiceImpl.getAuthUser();
        if (!toDoRepository.existsByIdAndUserId(id, user.getId())) return false;
        try {
            toDoRepository.deleteByIdAndUserId(id, user.getId());
            return !toDoRepository.existsByIdAndUserId(id, user.getId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeTodoList(List<ToDo> toDos) {
        User user = UserDetailsServiceImpl.getAuthUser();
        toDos.forEach(toDo -> toDoRepository.findByIdAndUserId(toDo.getId(), user.getId())
                .map((t) -> {
                    toDoRepository.deleteByIdAndUserId(toDo.getId(), user.getId());
                    return true;
                }).orElseThrow(() -> new RuntimeException("id not found " + toDo.getId()))
        );
        return true;
    }
}
