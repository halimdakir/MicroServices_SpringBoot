package com.iths.demo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {

    @Override
    public EntityModel<Book> toModel(Book book) {
        return new EntityModel<>(book,
                linkTo(methodOn(BookController.class).getOne(book.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).getAll()).withRel("books"));
    }

    @Override
    public CollectionModel<EntityModel<Book>> toCollectionModel(Iterable<? extends Book> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel).collect(Collectors.toList());
        return new CollectionModel<>(collection,
                linkTo(methodOn(BookController.class).getAll()).withSelfRel());
    }
}
