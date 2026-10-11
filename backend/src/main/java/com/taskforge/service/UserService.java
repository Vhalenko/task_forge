package com.taskforge.service;

import com.taskforge.mapper.UserMapper;
import com.taskforge.model.dto.TaskDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.entity.Task;
import com.taskforge.model.entity.User;
import com.taskforge.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository _userRepository;
    private final UserMapper _userMapper;
    public UserService(UserRepository userRepository, UserMapper userMapper) {

        _userRepository = userRepository;
        _userMapper = userMapper;
    }

    public List<UserDTO> getAll() {
        return _userRepository.findAll()
                .stream()
                .map(_userMapper::toUserDto)
                .toList();
    }

    public UserDTO getById(Long id) {
        return _userMapper.toUserDto(_userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("no users with id: " + id)));
    }
}
