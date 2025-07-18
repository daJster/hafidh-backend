package com.hafidh.service;

import com.hafidh.dto.JwtResponse;
import com.hafidh.dto.LoginRequest;
import com.hafidh.dto.SignupRequest;
import com.hafidh.entity.User_old;
import com.hafidh.repository.UserRepository;
import com.hafidh.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public JwtResponse register(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent() ){
            throw new EmailAlreadyExisteException();
        }


        User_old user = new User_old();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new JwtResponse(token);
    }

    public JwtResponse authenticate(LoginRequest request) {
        User_old user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new JwtResponse(token);
    }


}
