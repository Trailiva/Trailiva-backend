package com.trailiva.security;

import com.trailiva.data.model.Role;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableMongoAuditing
@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityTest {

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private CustomUserDetailService customUserDetailsService;

    private User mockedUser;

    @BeforeEach
    void setUp() {
        mockedUser = new User();
        mockedUser.setFirstName("Ismail");
        mockedUser.setLastName("Abdullah");
        mockedUser.setEmail("ohida2001@gmail.com");
        mockedUser.setPassword("pass1234");
        mockedUser.getRoles().add(Role.USER);
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
}