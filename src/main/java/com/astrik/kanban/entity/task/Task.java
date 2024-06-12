package com.astrik.kanban.entity.task;

import com.astrik.kanban.entity.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    @NotNull(message = "the id is needed to update", groups = UpdateTask.class)
    private Long id;

    @Column(name = "text", nullable = false)
    @NotBlank(message = "The text cannot be blank.", groups = {CreateTask.class, UpdateTask.class})
    private String text;

    @Column(name = "completed", columnDefinition = "boolean default false")
    @NotNull(message = "The completed property must have a boolean value.", groups = {CreateTask.class, UpdateTask.class})
    private Boolean completed;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    public Task(String text, Boolean completed) {
        this.text = text;
        this.completed = completed;
    }

    public Task() {

    }

    public Task(String text, Boolean completed, User user) {
        this.text = text;
        this.completed = completed;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
