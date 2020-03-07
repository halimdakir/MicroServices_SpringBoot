package com.iths.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(AuthorController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorControllerTest {
    private static String URL_TEMPLATE = "/authors/";
    @Autowired private ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @MockBean AuthorRepository repository;


    private List<Author> insertAuthor(){
        var author1 = new Author(1L, "Halim Dakir");
        var author2 = new Author(2L, "Nora Dakir");
        List<Author> authorList = Collections.synchronizedList(new ArrayList<>());
        authorList.add(author1);
        authorList.add(author2);
        return authorList;
    }

    @BeforeEach
    public void setUp(){
        List<Author> authorList = insertAuthor();
        when(repository.findAll()).thenReturn(List.of(authorList.get(0), authorList.get(1)));
        when(repository.findById(1L)).thenReturn(Optional.of(authorList.get(0)));
        when(repository.save(any(Author.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var b = (Author) args[0];
            return new Author(1L, b.getFullName());
        });
    }

    @Test
    @Order(1)
    @DisplayName("Get ALL ")
    public void getAll() throws Exception{
        mockMvc.perform(get(URL_TEMPLATE).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].fullName", Matchers.is("Halim Dakir")))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[1].fullName", Matchers.is("Nora Dakir")));
    }

    @Test
    @Order(2)
    @DisplayName("Get Request valid id ")
    public void getOneValidId() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE+1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.fullName", Matchers.is("Halim Dakir")));
    }
    @Test
    @Order(3)
    @DisplayName("Get Request invalid id")
    public void getOneInvalidId() throws Exception {
        mockMvc.perform(get("/authors/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("Post Request")
    public void createNewAuthor() throws Exception{
        var author = new Author(0L, "William Robbie");
        mockMvc.perform(post(URL_TEMPLATE).contentType("application/json")
                .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(5)
    @DisplayName("Put Request valid id")
    public void putAuthorValidID() throws Exception{
        var author = new Author(0L, "Name Unknown");
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(author))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("{\"id\":1,\"fullName\":\"Name Unknown\"}"));
        result.andExpect(status().is(200));
    }
    @Test
    @Order(6)
    @DisplayName("Put Request invalid id")
    public void putAuthorInvalidID() throws Exception{
        var author = new Author(0L, "Name Unknown");
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+11)
                        .content(objectMapper.writeValueAsString(author))
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @DisplayName("Patch Request valid id")
    public void patchAuthorValidID() throws Exception{
        var author = new Author(0L, "Name Unknown");
        final ResultActions result =
                mockMvc.perform(patch(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(author))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("{\"id\":1,\"fullName\":\"Name Unknown\"}"));
        result.andExpect(status().isOk());
    }
    /*@Test
    @Order(11)
    @DisplayName("Patch Request valid id and null value")
    public void patchAuthorValidIDNullValue() throws Exception{
        var author = new Author(0L, null);
        final ResultActions result =
                mockMvc.perform(patch(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(author))
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }*/
    @Test
    @Order(8)
    @DisplayName("Patch Request invalid id")
    public void patchAuthorInvalidID() throws Exception{
        var author = new Author(0L, "Name Unknown");
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+20)
                        .content(objectMapper.writeValueAsString(author))
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("Delete Request valid id")
    public void deleteAuthorValidID() throws Exception{
        mockMvc.perform(delete(URL_TEMPLATE+1))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
    @Test
    @Order(10)
    @DisplayName("Delete Request invalid id")
    public void deleteAuthorInvalidID() throws Exception {
        mockMvc.perform(delete(URL_TEMPLATE+20))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}