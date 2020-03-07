package com.iths.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@Entity
public class Author {
    @Id @GeneratedValue Long id;
    @NotNull @NotEmpty String fullName;

    public Author(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}
