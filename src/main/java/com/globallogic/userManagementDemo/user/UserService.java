package com.globallogic.userManagementDemo.user;

import com.globallogic.userManagementDemo.exception.InvalidPasswordException;
import com.globallogic.userManagementDemo.exception.UserAlreadyExistsException;
import com.globallogic.userManagementDemo.exception.UserNotFoundException;
import com.globallogic.userManagementDemo.security.JwtService;
import com.globallogic.userManagementDemo.user.domain.Phone;
import com.globallogic.userManagementDemo.user.domain.User;
import com.globallogic.userManagementDemo.user.dto.LoginResponse;
import com.globallogic.userManagementDemo.user.dto.SignUpRequest;
import com.globallogic.userManagementDemo.user.dto.SignUpResponse;
import com.globallogic.userManagementDemo.user.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class    UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    //Exactly one uppercase, two numbers, some lowercase letters, max length 12 and min 8.
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$)(?=(?:\\D*\\d){2}\\D*$)[a-zA-Z0-9]{8,12}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);


    public SignUpResponse signUp(SignUpRequest request) {
        if (!isValidPassword(request.getPassword())) {
            throw new InvalidPasswordException("Invalid password. It must have: Exactly one uppercase, two numbers, some lowercase letters, max length 12 and min 8.");
        }

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User with email '" + request.getEmail() + "' already exists.");
        });

        User newUser = userMapper.toUser(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        LocalDateTime now = LocalDateTime.now();
        newUser.setCreated(now);
        newUser.setLastLogin(now);
        newUser.setIsActive(true);

        if (request.getPhones() != null && !request.getPhones().isEmpty()) {
            List<Phone> phones = request.getPhones().stream()
                    .map(phoneRequest -> {
                        Phone phone = userMapper.toPhone(phoneRequest);
                        phone.setUser(newUser);
                        return phone;
                    })
                    .collect(Collectors.toList());
            newUser.setPhones(phones);
        }

        User savedUser = userRepository.save(newUser);
        String token = jwtService.generateToken(savedUser.getId().toString());
        savedUser.setToken(token);
        savedUser = userRepository.save(savedUser);

        return userMapper.toSignUpResponse(savedUser);
    }

    public LoginResponse loginUserByToken(String tokenString) {
        String userIdFromToken;
        try {
            userIdFromToken = jwtService.extractSubject(tokenString);
            if (userIdFromToken == null || userIdFromToken.isEmpty()) {
                throw new RuntimeException("Token subject (user ID) is missing or invalid.");
            }
        } catch (RuntimeException e) {
            throw e;
        }

        User user = userRepository.findById(UUID.fromString(userIdFromToken))
                .orElseThrow(() -> new UserNotFoundException("User not found for token subject: " + userIdFromToken));

        LocalDateTime oldLastLogin = user.getLastLogin();

        user.setLastLogin(LocalDateTime.now());
        String newToken = jwtService.generateToken(user.getId().toString());
        user.setToken(newToken);
        User updatedUser = userRepository.save(user);

        LoginResponse response = userMapper.toLoginResponse(updatedUser);
        response.setLastLogin(oldLastLogin);

        return response;
    }

    private boolean isValidPassword(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }
}