package com.astrik.kanban.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astrik.kanban.entity.User;
import com.astrik.kanban.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ToDoService toDoService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       ToDoService toDoService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.toDoService = toDoService;
    }

    public List<User> reedUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return new User("this user is already registered");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User userCreated = userRepository.save(user);
            return userCreated;
        }
    }

    public User updateUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return new User("this user is already registered");
        else {
            User userToUpdate = UserDetailsServiceImpl.getAuthUser();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return userRepository.save(userToUpdate);
        }
    }

    public boolean removeUser() {
        User userToDelete = UserDetailsServiceImpl.getAuthUser();
        if (userToDelete != null) {
            userRepository.delete(userToDelete);
            return !userRepository.existsById(userToDelete.getId());
        } else return false;
    }

}
