package com.trailiva.security;

import com.trailiva.data.model.Role;
import com.trailiva.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private CustomUserDetailService customUserDetailsService;

    private com.trailiva.data.model.User mockedUser;

    @BeforeEach
    void setUp() {
        mockedUser = new com.trailiva.data.model.User();
        mockedUser.setFirstName("Ismail");
        mockedUser.setLastName("Abdullah");
        mockedUser.setEmail("ohida2001@gmail.com");
        mockedUser.setPassword("pass1234");
        Role role = new Role("ROLE_USER");
        mockedUser.getRoles().add(role);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        userRepository = null;
        customUserDetailsService = null;
    }

    /**
     * User details service test
     */

    @Test
    @DisplayName("User details can be fetch from database by email with role User")
    void user_canFetchDataFromDbByEmail() {
        when(userRepository.findByEmail("ohida2001@gmail.com"))
                .thenReturn(Optional.of(mockedUser));

        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername("ohida2001@gmail.com");

        verify(userRepository, times(1)).findByEmail("ohida2001@gmail.com");

        assertNotNull(fetchedUser);
        assertAll(
                () -> assertEquals(fetchedUser.getFirstName(), mockedUser.getFirstName()),
                () -> assertEquals(fetchedUser.getLastName(), mockedUser.getLastName()),
                () -> assertEquals(fetchedUser.getEmail(), mockedUser.getEmail()),
                () -> assertEquals(fetchedUser.getUsername(), mockedUser.getFirstName() + " " + mockedUser.getLastName()),
                () -> assertEquals(fetchedUser.getPassword(), mockedUser.getPassword()),
                () -> assertEquals(fetchedUser.getAuthorities().size(), 1)
        );
    }

    /**
     * Jwt Token Test
     */

    @Test
    @DisplayName("Jwt token can be generated")
    void jwt_tokenCanBeGenerated() {
        //Given
        when(userRepository.findByEmail("ohida2001@gmail.com"))
                .thenReturn(Optional.of(mockedUser));
        when(jwtTokenProvider.generateToken(any())).thenReturn(UUID.randomUUID().toString());

        //When
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername("ohida2001@gmail.com");
        String actualToken = jwtTokenProvider.generateToken(fetchedUser);

        //Assert
        assertNotNull(actualToken);
        assertEquals(actualToken.getClass(), String.class);
    }

    @Test
    @DisplayName("Username can be extracted from jwt token")
    void can_extractUsernameFromJwtToken() {
        String username = mockedUser.getFirstName() + " " + mockedUser.getLastName();
        //Given
        when(userRepository.findByEmail("ohida2001@gmail.com"))
                .thenReturn(Optional.of(mockedUser));
        when(jwtTokenProvider.generateToken(any())).thenReturn(UUID.randomUUID().toString());
        when(jwtTokenProvider.extractEmail(anyString())).thenReturn("Ismail Abdullah");
        //When
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername("ohida2001@gmail.com");
        String jwtToken = jwtTokenProvider.generateToken(fetchedUser);
        String actual = jwtTokenProvider.extractEmail(jwtToken);

        //Assert
        assertEquals(username, actual);
    }

    @Test
    @DisplayName("Token can be validated by checking expiration date")
    void test_thatTokenHasNotExpire() {
        //Given
        when(userRepository.findByEmail("ohida2001@gmail.com"))
                .thenReturn(Optional.of(mockedUser));

        //When
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername("ohida2001@gmail.com");
        String jwtToken = jwtTokenProvider.generateToken(fetchedUser);
        boolean hasExpire = jwtTokenProvider.isTokenExpired(jwtToken);

        //Assert
        assertFalse(hasExpire);
    }

    @Test
    @DisplayName("Jwt token can be validated by username and expiration date")
    void test_jwtTokenCanBeValidated() {
        //Given
        when(userRepository.findByEmail("ohida2001@gmail.com"))
                .thenReturn(Optional.of(mockedUser));

        //When
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername("ohida2001@gmail.com");
        String jwtToken = jwtTokenProvider.generateToken(fetchedUser);
        boolean isValid = jwtTokenProvider.validateToken(jwtToken, fetchedUser);

        //Assert
        assertFalse(isValid);
    }
}