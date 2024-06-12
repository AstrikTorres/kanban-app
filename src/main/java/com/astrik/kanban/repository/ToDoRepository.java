package com.astrik.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.astrik.kanban.entity.task.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
