package com.taskforge.service;

import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository _userRepository;
    public UserService(UserRepository userRepository) {
        _userRepository = userRepository;
    }
}
