package com.iths.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
public class SetUpDataBase {

    @Bean
    CommandLineRunner initDatabase(AuthorRepository authorRepository, BookRepository bookRepository) {
        return args -> {
            if (authorRepository.count() == 0 && bookRepository.count() == 0) {
                List<Book> bookList = books();
                List<Author> authorList = authors();
                for (Author author: authorList){
                    authorRepository.save(author);
                }
                for (Book book: bookList){
                    bookRepository.save(book);
                }
            }
        };
    }
    private List<Author> authors(){
        var author1 = new Author(0L, "Halim Dakir");
        var author2 = new Author(0L, "Nora Dakir");
        var author3 = new Author(0L, "Loca Modric");
        List<Author> authorList = Collections.synchronizedList(new ArrayList<>());
        authorList.add(author1);
        authorList.add(author2);
        authorList.add(author3);
        return authorList;
    }
    private List<Book> books(){
        var book1 = new Book(0L, "SpringBoot", 2L);
        var book2 = new Book(0L, "CSharp", 1L);
        var book3 = new Book(0L, "JavaSE", 3L);
        List<Book> bookList  = Collections.synchronizedList(new ArrayList<>());
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);
        return bookList;
    }
}
