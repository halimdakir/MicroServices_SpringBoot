package com.iths.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(BookController.class)
@Import({BookModelAssembler.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest {
    private static String BASE_URL = "http://localhost/books";
    private static String URL_TEMPLATE = "/books/";
    private static String CONTENT_TYPE = "application/json";
    private static String EXPRESSION = "_links.self.href";
    private static String EXPRESSION1 = "_links.books.href";
    @Autowired private ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @MockBean BookRepository bookRepository;


    private List<Book> insertBook(){
        var book1 = new Book(1L, "SpringBoot", 2L);
        var book2 = new Book(2L, "CSharp", 1L);
        List<Book> bookList  = Collections.synchronizedList(new ArrayList<>());
        bookList.add(book1);
        bookList.add(book2);
        return bookList;
    }
    @BeforeEach
    void setUp() {
        List<Book> bookList = insertBook();
        when(bookRepository.findAll()).thenReturn(List.of(bookList.get(0), bookList.get(1)));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookList.get(0)));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var b = (Book) args[0];
            return new Book(1L, b.getTitle(), b.getAuthorId());
        });
    }

    @Test
    @Order(1)
    @DisplayName("Get all books")
    void getAllBooks() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.bookList[0]."+EXPRESSION1, Matchers.is(BASE_URL)))
                .andExpect(jsonPath("_embedded.bookList[0].title", Matchers.is("SpringBoot")))
                .andExpect(jsonPath("_embedded.bookList[1].title", Matchers.is("CSharp")));
    }

    @Test
    @Order(3)
    @DisplayName("Get one book with valid ID")
    void getOneBookValidId() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE+1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath(EXPRESSION, is(BASE_URL+"/"+1)));
    }
    @Test
    @Order(2)
    @DisplayName("Get one book with invalid ID")
    void getOneBookInValidId() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE+10).contentType(CONTENT_TYPE)).andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("Post request")
    void createBook() throws Exception {
        var book = new Book(0L, "JavaSE", 3L);
        mockMvc.perform(post(URL_TEMPLATE).contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(7)
    @DisplayName("Put Request valid ID")
    void putValidIdTest() throws Exception {
        var book = new Book(2L, "Asp.Net", 2L);
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("{\"id\":1,\"title\":\"Asp.Net\",\"authorID\":2}"));
        result.andExpect(status().is(201));
    }
    @Test
    @Order(8)
    @DisplayName("Put Request invalid ID")
    void putInvalidIdTest() throws Exception {
        var book = new Book(3L, "Asp.Net", 3L);
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+3)
                                .content(objectMapper.writeValueAsString(book))
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("Patch Request valid ID")
    void patchValidIdTest() throws Exception {
        var book = new Book(0L, "Asp.Net", 1L);
        final ResultActions result =
                mockMvc.perform(patch(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string("{\"id\":1,\"title\":\"Asp.Net\",\"authorID\":1}"));
        result.andExpect(status().isOk());
    }

    /*@Test
    @Order(11)
    @DisplayName("Patch Request valid ID and Null name")
    void patchValidIdNullTitleTest() throws Exception {
        var book = new Book(1L, "", null);
        final ResultActions result =
                mockMvc.perform(patch(URL_TEMPLATE+1)
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }*/

    @Test
    @Order(10)
    @DisplayName("Patch Request invalid ID")
    void patchInvalidIdTest() throws Exception {
        var book = new Book(4L, "Asp.Net", 4L);
        final ResultActions result =
                mockMvc.perform(put(URL_TEMPLATE+4)
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("Delete Request valid ID")
    void deleteValidIdTest() throws Exception {
        mockMvc.perform(delete(URL_TEMPLATE+1))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @Order(6)
    @DisplayName("Delete Request invalid id")
    void deleteInvalidIdTest() throws Exception {
        mockMvc.perform(delete(URL_TEMPLATE+20))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
