package com.trailiva.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.service.AuthService;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private String registerJsonObject;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        UserRequest userDto = new UserRequest();
        userDto.setFirstName("Ismail");
        userDto.setLastName("Abdul");
        userDto.setEmail("ismail1@gmail.com");
        userDto.setPassword("password123");
        registerJsonObject = objectMapper.writeValueAsString(userDto);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void whenUserRegisterWithValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post("/api/v1/trailiva/auth/register")
                        .contentType("application/json")
                        .content(registerJsonObject)).andDo(print())
                .andExpect(status().isCreated());
    }


    @Test
    void whenUserLoginWithValidInput_thenReturns200() throws Exception {
        //Given
        LoginRequest loginDto = new LoginRequest("test@gmail.com", "test123");

        //When
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/trailiva/auth/login")
                        .contentType("application/json").content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        //Then
        int expectedStatus = 200;
        int actualStatus = mvcResult.getResponse().getStatus();
        assertThat(expectedStatus).isEqualTo(actualStatus);
    }



    @Test
    void whenUserUpdatePasswordWithValidInput_thenReturns200() throws Exception {
        PasswordRequest passwordRequest = new PasswordRequest("user@gmail.com", "pass123", "password123");
        mockMvc.perform(post("/api/v1/trailiva/auth/password/update")
                        .contentType("application/json").content(objectMapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenUserForgetPassword_thenReturn201() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/trailiva/auth/password/reset/whale")
                        .contentType("application.json"))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        //Then
        int expectedStatus = 201;
        int actualStatus = mvcResult.getResponse().getStatus();
        assertThat(expectedStatus).isEqualTo(actualStatus);
    }

    @Test
    void whenUserResetPassword_theReturn200() throws Exception{
        //Given
//        UpdatePasswordRequest passwordReset = new UpdatePasswordRequest("test@gmail.com", "test123");

        //When
//        MvcResult mvcResult = mockMvc.perform(post("/api/v1/trailiva/auth/password/reset/93j34fh8wnj43n8a")
//                        .contentType("application/json").content(objectMapper.writeValueAsString(passwordReset)))
//                .andDo(print())
//                .andExpect(status().isOk()).andReturn();

        //Then
//        int expectedStatus = 200;
//        int actualStatus = mvcResult.getResponse().getStatus();
//        assertThat(expectedStatus).isEqualTo(actualStatus);
    }
}