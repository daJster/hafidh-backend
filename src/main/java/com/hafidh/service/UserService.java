package com.hafidh.service;

import com.hafidh.dto.user.UserDTO;
import com.hafidh.dto.user.UserProfileDTO;
import com.hafidh.entity.User_old;
import com.hafidh.mapper.UserMapper;
import com.hafidh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserMapper userMapper;

    @Cacheable(value = "user", key = "#userId")
    public UserDTO getUserById(Long userId) {
        User_old user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }
    @Cacheable(value = "user", key = "#userEmail")
    public UserDTO getUserByEmail(String email) {
        User_old user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    @CacheEvict(value = "user", key = "#userId")
    public void updateUserProfile(Long userId, UserProfileDTO dto) {
        User_old user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }
}