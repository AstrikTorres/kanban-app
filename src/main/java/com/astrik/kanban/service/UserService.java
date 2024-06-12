package com.astrik.kanban.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.astrik.kanban.entity.User;
import com.astrik.kanban.repository.UserRepository;

import java.util.List;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
