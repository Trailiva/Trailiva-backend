package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.TokenRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.security.CustomUserDetailService;
import com.trailiva.security.JwtTokenProvider;
import com.trailiva.security.UserPrincipal;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.RoleNotFoundException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.*;
import com.trailiva.web.payload.response.JwtTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailService customUserDetailsService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    private  User mockedUser;

    @InjectMocks
    private AuthServiceImpl authService;


    @BeforeEach
    void setUp() {
        mockedUser = new User();
        mockedUser.setUserId(1L);
        mockedUser.setFirstName("Ismail");
        mockedUser.setLastName("Abdullah");
        mockedUser.setEmail("ismail@gmail.com");
        mockedUser.setPassword("pass1234");
        Role role = new Role(RoleName.ROLE_USER);
        mockedUser.getRoles().add(role);
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void userCanRegister() throws AuthException, MessagingException, UnsupportedEncodingException, RoleNotFoundException {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("ismail@gmail.com");
        EmailRequest emailRequest = new EmailRequest();
        //Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(userRequest, User.class)).thenReturn(mockedUser);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);
        doNothing().when(emailService).sendUserVerificationEmail(any());

        //When
       authService.register(userRequest, "");

        //Assert
        verify(userRepository, times(1)).existsByEmail(mockedUser.getEmail());
        verify(userRepository, times(1)).save(mockedUser);
    }


    @Test
    void whenLoginMethodIsCalled_ThenFindUserByEmailIsCalledOnce() {
        //Given
        LoginRequest loginRequest = new LoginRequest("ismail@gmail.com", "password123");
        when(userRepository.findByEmail("ismail@gmail.com")).thenReturn(Optional.of(mockedUser));

        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword());
        testingAuthenticationToken.setAuthenticated(true);
        testingAuthenticationToken.setDetails(loginRequest);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword())
        )).thenReturn(testingAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

        UserPrincipal principal = modelMapper.map(mockedUser, UserPrincipal.class);


        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        String actualToken = jwtTokenProvider.generateToken(fetchedUser);

        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(fetchedUser);
        when(jwtTokenProvider.generateToken(any(UserPrincipal.class))).thenReturn(actualToken);

        JwtTokenResponse jwtTokenResponse = authService.login(loginRequest);
        verify(customUserDetailsService, times(2)).loadUserByUsername(loginRequest.getEmail());
        verify(jwtTokenProvider, times(2)).generateToken(principal);
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());

        assertNotNull(jwtTokenResponse);
        assertEquals(jwtTokenResponse.getJwtToken(), actualToken);
        assertEquals(jwtTokenResponse.getEmail(), loginRequest.getEmail());
    }


    @Test
    @DisplayName("Saved user can update password")
    void checkIfSavedUserCanUpdatePassword() throws AuthException {
        String randomEncoder = UUID.randomUUID().toString();
        //Given
        PasswordRequest passwordRequest = new PasswordRequest("ismail@gmail.com", "password123", "pass1234");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(randomEncoder);

        //When
        String expected = passwordRequest.getOldPassword();
        String actual = mockedUser.getPassword();
        authService.updatePassword(passwordRequest);

        //Assert
        verify(passwordEncoder, times(1)).matches(expected, actual);
        verify(passwordEncoder, times(1)).encode(passwordRequest.getPassword());
        verify(userRepository, times(1)).findByEmail(passwordRequest.getEmail());
        verify(userRepository, times(1)).save(mockedUser);

        assertNotEquals(expected, mockedUser.getPassword());
        assertEquals(randomEncoder, mockedUser.getPassword());
    }

    @Test
    void whenLoginMethodIsCalled_withNullEmail_NullPointerExceptionIsThrown(){
        LoginRequest loginDto = new LoginRequest();
        when(userRepository.findByEmail(loginDto.getEmail())).thenThrow(new NullPointerException("User email cannot be null"));
        verify(userRepository, times(0)).findByEmail(loginDto.getEmail());
    }

    @Test
    void whenLoginMethodIsCalled_withNullPassword_NullPointerExceptionIsThrown(){
        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("whalewalker@gmail.com");
        when(userRepository.findByEmail(loginDto.getEmail())).thenThrow(new NullPointerException("User password cannot be null"));
        verify(userRepository, times(0)).findByEmail(loginDto.getEmail());
    }

    @Test
    @DisplayName("Password reset token can be generated for user to reset password")
    void checkIfResetTokenCanBeGeneratedWhenUserWantToResetPassword() throws AuthException {
        //Given
        String email = mockedUser.getEmail();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));

        //When
        ArgumentCaptor<Token> tokenArgumentCaptor = ArgumentCaptor.forClass(Token.class);
        authService.generatePasswordResetToken(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        verify(tokenRepository, times(1)).save(tokenArgumentCaptor.capture());

        assertNotNull(tokenArgumentCaptor.getValue());
        assertNotNull(tokenArgumentCaptor.getValue().getToken());
        assertEquals(TokenType.PASSWORD_RESET, tokenArgumentCaptor.getValue().getType());
        assertNotNull(tokenArgumentCaptor.getValue().getUserId());
    }

    @Test
    @DisplayName("User can reset password")
    void savedUserCanResetPassword() throws AuthException, TokenException {
        // Given
        String randomEncoder = UUID.randomUUID().toString();
        ResetPasswordRequest passwordResetRequest = new ResetPasswordRequest("ismail@gmail.com", "pass1234");
        String passwordResetToken = UUID.randomUUID().toString();

        Token mockToken = new Token();
        mockToken.setId(1L);
        mockToken.setToken(passwordResetToken);
        mockToken.setType(TokenType.PASSWORD_RESET);
        mockToken.setUserId(1L);
        mockToken.setExpiry(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(mockToken));
        when(passwordEncoder.encode(anyString())).thenReturn(randomEncoder);

        ArgumentCaptor<User> tokenArgumentCaptor = ArgumentCaptor.forClass(User.class);
        authService.resetPassword(passwordResetRequest, passwordResetToken);

        verify(userRepository, times(1)).save(tokenArgumentCaptor.capture());
        verify(tokenRepository, times(1)).delete(mockToken);
        verify(passwordEncoder, times(1)).encode(passwordResetRequest.getPassword());

        assertThat(tokenArgumentCaptor.getValue()).isNotNull();
        assertThat(tokenArgumentCaptor.getValue().getPassword()).isNotNull();
    }


}