package com.iths.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class Book {
    /*@Getter @Setter*/ @Id @GeneratedValue Long id;
    /*@Getter @Setter*/ @NotNull @NotEmpty String title;
    /*@Getter @Setter*/ @NotNull Long authorId;

    public Book(Long id, String title, Long authorId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
    }
}
