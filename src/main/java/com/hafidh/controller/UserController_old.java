package com.hafidh.controller;

import com.hafidh.dto.user.UserDTO;
import com.hafidh.dto.user.UserProfileDTO;
import com.hafidh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController_old {

    @Autowired private UserService userService;

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody UserProfileDTO dto) {
        userService.updateUserProfile(id, dto);
    }

}