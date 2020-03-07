package com.iths.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorRepository repository;

    public AuthorController(AuthorRepository authorRepository) {
        this.repository = authorRepository;
    }

    @GetMapping
    public List<Author> getAll(){
        return repository.findAll();
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<Author> getOne(@PathVariable long id){
        var oneProduct = repository.findById(id);
        return oneProduct.map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    public ResponseEntity<Author> post(@RequestBody Author author){
        var author1 = repository.save(author);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BookController.class).slash(author1.getId()).toUri());
        return new ResponseEntity<>(author1, headers, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Author> put(@RequestBody Author newAuthor, @PathVariable Long id) {
        return repository.findById(id)
                .map(author -> {
                    author.setFullName(newAuthor.getFullName());
                    repository.save(author);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(BookController.class).slash(author.getId()).toUri());
                    return new ResponseEntity<>(author, headers, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Author> patch(@RequestBody Author newAuthor, @PathVariable Long id){
        return repository.findById(id)
                .map(author -> {
                    if (newAuthor.getFullName() != null)
                        author.setFullName(newAuthor.getFullName());
                        repository.save(author);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setLocation(linkTo(BookController.class).slash(author.getId()).toUri());
                        return new ResponseEntity<>(author, headers, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") final long id) throws BookNotFoundException {
        return repository.findById(id)
                .map(p -> {
                    repository.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
