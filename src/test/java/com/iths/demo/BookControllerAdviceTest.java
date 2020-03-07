package com.iths.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookControllerAdvice.class)
class BookControllerAdviceTest {
    private static String URL_TEMPLATE = "/books/";
    @Autowired private ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @MockBean BookRepository repository;

    @Before
    void init(){
    }
    @Test
    void notFoundException() throws Exception {
        mockMvc.perform(delete(URL_TEMPLATE+10).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}