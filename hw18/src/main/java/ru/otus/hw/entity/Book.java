package ru.otus.hw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.otus.hw.utils.Constants.BOOKS_ENTITY_GRAPH;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(name = BOOKS_ENTITY_GRAPH,
        attributeNodes = {@NamedAttributeNode("author"), @NamedAttributeNode("genre")})
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Author author;

    @JoinColumn(name = "genre_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;
}
