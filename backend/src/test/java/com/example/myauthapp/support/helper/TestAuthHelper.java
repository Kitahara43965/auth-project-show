package com.example.myauthapp.support.helper;

import com.example.myauthapp.auth.dto.LoginRequest;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestAuthHelper {

    private final MockMvc mockMvc;

    public TestAuthHelper(
        MockMvc mockMvc
    ) {
        this.mockMvc = mockMvc;
    }

    public MockHttpServletResponse login(
        LoginRequest loginRequest
    ) throws Exception {

        return mockMvc.perform(
            post("/api/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                    ))
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();
    }
}
