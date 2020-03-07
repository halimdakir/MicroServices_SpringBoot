package com.iths.demo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/books")
public class BookController {
    private final BookRepository bookRepository;
    private final BookModelAssembler bookModelAssembler;


    public BookController(BookRepository bookRepository, BookModelAssembler bookModelAssembler) {
        this.bookRepository = bookRepository;
        this.bookModelAssembler = bookModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Book>> getAll(){           //List<Book> getAll()
        return bookModelAssembler.toCollectionModel(bookRepository.findAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Book>> getOne(@PathVariable long id){
        return bookRepository.findById(id)
                .map(bookModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Book> post(@RequestBody Book book){
        var book1 = bookRepository.save(book);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BookController.class).slash(book1.getId()).toUri());
        return new ResponseEntity<>(book1, headers, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Book> put(@RequestBody Book newBook, @PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(newBook.getTitle());
                    bookRepository.save(book);
                    final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                    return ResponseEntity.created(uri).body(book);
                    })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Book> patch(@RequestBody Book newBook, @PathVariable Long id){
        return bookRepository.findById(id)
                .map(book -> {
                    if (newBook.getTitle() != null && newBook.getAuthorId() != null)
                        book.setTitle(newBook.getTitle());
                        book.setAuthorId(newBook.getAuthorId());
                    bookRepository.save(book);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(BookController.class).slash(book.getId()).toUri());
                    return new ResponseEntity<>(book, headers, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") final long id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .map(p -> {
                            bookRepository.deleteById(id);
                           return new ResponseEntity<>(HttpStatus.OK);
                        })
                .orElseThrow(() -> new BookNotFoundException(" ID : '"+id+"' Does not exist")); //Customize it
    }
}
