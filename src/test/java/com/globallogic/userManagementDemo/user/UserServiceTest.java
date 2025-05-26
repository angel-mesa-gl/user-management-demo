package com.globallogic.userManagementDemo.user;

import com.globallogic.userManagementDemo.exception.InvalidPasswordException;
import com.globallogic.userManagementDemo.exception.UserAlreadyExistsException;
import com.globallogic.userManagementDemo.exception.UserNotFoundException;
import com.globallogic.userManagementDemo.security.JwtService;
import com.globallogic.userManagementDemo.user.domain.Phone;
import com.globallogic.userManagementDemo.user.domain.User;
import com.globallogic.userManagementDemo.user.dto.LoginResponse;
import com.globallogic.userManagementDemo.user.dto.PhoneRequest;
import com.globallogic.userManagementDemo.user.dto.SignUpRequest;
import com.globallogic.userManagementDemo.user.dto.SignUpResponse;
import com.globallogic.userManagementDemo.user.mapper.UserMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private String jwtToken;
    private String loginJwtToken;
    private String encodedPassword;

    private LocalDateTime testFixedDateTime;
    private LocalDateTime userCreationDateTime;
    private LocalDateTime userLastLoginDateTime;

    private SignUpRequest validUserSignUpRequest;
    private User userMappedFromRequest;
    private PhoneRequest validPhoneRequest;
    private Phone mappedPhoneEntity;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        jwtToken = "mock.jwt.token";
        loginJwtToken = "valid.mock.login.token";
        encodedPassword = "hashedPassword";

        testFixedDateTime = LocalDateTime.of(2025, 5, 20, 10, 0, 0, 0);
        userCreationDateTime = testFixedDateTime.minusDays(5);
        userLastLoginDateTime = testFixedDateTime.minusHours(1);

        validPhoneRequest = new PhoneRequest(87650009L,
                7,
                "25");
        validUserSignUpRequest = new SignUpRequest(
                "Julio Gonzalez",
                "julio@testssw.cl",
                "a2asfGfdfdf4",
                List.of(validPhoneRequest)
        );

        userMappedFromRequest = new User();
        userMappedFromRequest.setName(validUserSignUpRequest.getName());
        userMappedFromRequest.setEmail(validUserSignUpRequest.getEmail());
        userMappedFromRequest.setPassword(validUserSignUpRequest.getPassword());
    }

    private User createValidUser(String token, boolean includePhone) {
        User user = new User(
                userId,
                validUserSignUpRequest.getName(),
                validUserSignUpRequest.getEmail(),
                validUserSignUpRequest.getPassword(),
                userCreationDateTime,
                userLastLoginDateTime,
                token,
                true,
                Collections.emptyList()
        );
        if (includePhone) {
            Phone phone = new Phone(1L, validPhoneRequest.getNumber(), validPhoneRequest.getCitycode(), validPhoneRequest.getCountrycode(), user);
            user.setPhones(List.of(phone));
        } else {
            user.setPhones(Collections.emptyList());
        }
        return user;
    }

    private void setupMocksForSuccessfulSignUp(boolean includePhone) {
        User savedUserEntity = createValidUser(jwtToken, includePhone);
        SignUpResponse signUpResponseResult = new SignUpResponse(userId, testFixedDateTime, testFixedDateTime, jwtToken, true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.toUser(any(SignUpRequest.class))).thenReturn(userMappedFromRequest);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        if (includePhone) {
            mappedPhoneEntity = new Phone(1L, validPhoneRequest.getNumber(), validPhoneRequest.getCitycode(), validPhoneRequest.getCountrycode(), savedUserEntity);
            when(userMapper.toPhone(any(PhoneRequest.class))).thenReturn(mappedPhoneEntity);
        } else {
            verify(userMapper, never()).toPhone(any(PhoneRequest.class));
            verify(userMapper, never()).toPhoneList(anyList());
        }

        when(userRepository.save(any(User.class))).thenReturn(savedUserEntity);
        when(jwtService.generateToken(anyString())).thenReturn(jwtToken);
        when(userMapper.toSignUpResponse(any(User.class))).thenReturn(signUpResponseResult);
    }


    @Test
    void signUp_Success_WithPhones() {
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(testFixedDateTime);
            setupMocksForSuccessfulSignUp(true);

            SignUpResponse result = userService.signUp(validUserSignUpRequest);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(jwtToken, result.getToken());
            assertTrue(result.getIsActive());
            assertEquals(testFixedDateTime, result.getCreated());
            assertEquals(testFixedDateTime, result.getLastLogin());

            verify(userRepository).findByEmail(validUserSignUpRequest.getEmail());
            verify(userMapper).toUser(validUserSignUpRequest);
            verify(passwordEncoder).encode(validUserSignUpRequest.getPassword());
            verify(userMapper).toPhone(validPhoneRequest);
            verify(userRepository, times(2)).save(any(User.class));
            verify(jwtService).generateToken(userId.toString());
            verify(userMapper).toSignUpResponse(any(User.class));
        }
    }

    @Test
    void signUp_Success_NoPhones() {
        validUserSignUpRequest.setPhones(null);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(testFixedDateTime);

            setupMocksForSuccessfulSignUp(false);

            SignUpResponse result = userService.signUp(validUserSignUpRequest);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals(jwtToken, result.getToken());
            assertTrue(result.getIsActive());
            assertEquals(testFixedDateTime, result.getCreated());
            assertEquals(testFixedDateTime, result.getLastLogin());

            verify(userRepository).findByEmail(validUserSignUpRequest.getEmail());
            verify(userMapper).toUser(validUserSignUpRequest);
            verify(passwordEncoder).encode(validUserSignUpRequest.getPassword());
            verify(userMapper, never()).toPhone(any(PhoneRequest.class));
            verify(userRepository, times(2)).save(any(User.class));
            verify(jwtService).generateToken(userId.toString());
            verify(userMapper).toSignUpResponse(any(User.class));
        }
    }

    @Test
    void signUp_UserAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.signUp(validUserSignUpRequest));

        assertEquals("User with email 'julio@testssw.cl' already exists.", exception.getMessage());
        verify(userRepository).findByEmail(validUserSignUpRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, userRepository, jwtService, userMapper);
    }

    @Test
    void signUp_InvalidPassword() {
        validUserSignUpRequest.setPassword("invalidPass");

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                userService.signUp(validUserSignUpRequest));

        assertEquals("Invalid password. It must have: Exactly one uppercase, two numbers, some lowercase letters, max length 12 and min 8.", exception.getMessage());
        verifyNoInteractions(userRepository, jwtService, userMapper);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void loginUserByToken_Success() {
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(testFixedDateTime);

            User userBeforeUpdate = createValidUser(jwtToken, false);

            User userAfterUpdateInDb = new User(
                    userId,
                    userBeforeUpdate.getName(),
                    userBeforeUpdate.getEmail(),
                    userBeforeUpdate.getPassword(),
                    userBeforeUpdate.getCreated(),
                    testFixedDateTime,
                    "new.mock.jwt.token",
                    true,
                    userBeforeUpdate.getPhones()
            );

            LoginResponse loginResponseResult = new LoginResponse(
                    userId,
                    userBeforeUpdate.getCreated(),
                    userBeforeUpdate.getLastLogin(),
                    "new.mock.jwt.token",
                    true,
                    userBeforeUpdate.getName(),
                    userBeforeUpdate.getEmail(),
                    userBeforeUpdate.getPassword(),
                    userMapper.toPhoneResponseList(userBeforeUpdate.getPhones())
            );

            when(jwtService.extractSubject(loginJwtToken)).thenReturn(userId.toString());
            when(userRepository.findById(userId)).thenReturn(Optional.of(userBeforeUpdate));
            when(jwtService.generateToken(anyString())).thenReturn("new.mock.jwt.token");
            when(userRepository.save(any(User.class))).thenReturn(userAfterUpdateInDb);
            when(userMapper.toLoginResponse(any(User.class))).thenReturn(loginResponseResult);

            LoginResponse result = userService.loginUserByToken(loginJwtToken);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals("new.mock.jwt.token", result.getToken());
            assertEquals(userLastLoginDateTime, result.getLastLogin());
            assertEquals(userCreationDateTime, result.getCreated());

            verify(jwtService).extractSubject(loginJwtToken);
            verify(userRepository).findById(userId);
            verify(jwtService).generateToken(userId.toString());
            verify(userRepository).save(any(User.class));
            verify(userMapper).toLoginResponse(any(User.class));
        }
    }


    @Test
    void loginUserByToken_ExpiredToken() {
        when(jwtService.extractSubject(loginJwtToken)).thenThrow(new ExpiredJwtException(null, null, "JWT token has expired"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.loginUserByToken(loginJwtToken));

        assertEquals("JWT token has expired", exception.getMessage());
        verify(jwtService).extractSubject(loginJwtToken);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void loginUserByToken_InvalidToken() {
        when(jwtService.extractSubject(loginJwtToken)).thenThrow(new MalformedJwtException("Invalid JWT token: Malformed JWT"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.loginUserByToken(loginJwtToken));

        assertTrue(exception.getMessage().startsWith("Invalid JWT token: Malformed JWT"));
        verify(jwtService).extractSubject(loginJwtToken);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void loginUserByToken_UserNotFound() {
        when(jwtService.extractSubject(loginJwtToken)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.loginUserByToken(loginJwtToken));

        assertEquals("User not found for token subject: " + userId.toString(), exception.getMessage());
        verify(jwtService).extractSubject(loginJwtToken);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void loginUserByToken_NullOrEmptySubject() {
        when(jwtService.extractSubject(loginJwtToken)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.loginUserByToken(loginJwtToken));

        assertEquals("Token subject (user ID) is missing or invalid.", exception.getMessage());
        verify(jwtService).extractSubject(loginJwtToken);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }
}