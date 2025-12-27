package io.pillopl.library.catalogue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a book definition in the catalogue.
 * <p>
 * This class captures the metadata of a book, such as its ISBN, title, and author.
 * It does not represent a physical copy on the shelf (see {@link BookInstance}).
 * </p>
 */
@Value
@EqualsAndHashCode(of = "bookIsbn")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class Book {

    /**
     * The ISBN of the book. Acts as a unique identifier for the book definition.
     */
    @NonNull
    private ISBN bookIsbn;

    /**
     * The title of the book.
     */
    @NonNull
    private Title title;

    /**
     * The author of the book.
     */
    @NonNull
    private Author author;

    /**
     * Constructs a new Book.
     *
     * @param isbn   the ISBN string.
     * @param author the author's name.
     * @param title  the book's title.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    Book(String isbn, String author, String title) {
        this(new ISBN(isbn), new Title(title), new Author(author));
    }
}


/**
 * Value object representing a book's title.
 */
@Value
class Title {

    /**
     * The raw title string.
     */
    @NonNull String title;

    /**
     * Constructs a Title.
     *
     * @param title the title string. Must not be empty.
     * @throws IllegalArgumentException if the title is empty.
     */
    Title(String title) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title.trim();
    }

}

/**
 * Value object representing a book's author.
 */
@Value
class Author {

    /**
     * The author's name.
     */
    @NonNull String name;

    /**
     * Constructs an Author.
     *
     * @param name the author's name. Must not be empty.
     * @throws IllegalArgumentException if the name is empty.
     */
    Author(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        this.name = name.trim();
    }
}