package io.pillopl.library.catalogue;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Internal repository for accessing the Catalogue database.
 * <p>
 * This class handles the persistence of {@link Book} and {@link BookInstance} entities using JDBC.
 * It is package-private to enforce access via the {@link Catalogue} service.
 * </p>
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class CatalogueDatabase {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Persists a new book definition.
     *
     * @param book the book to save.
     * @return the saved book.
     */
    Book saveNew(Book book) {
        jdbcTemplate.update("" +
                        "INSERT INTO catalogue_book " +
                        "(id, isbn, title, author) VALUES " +
                        "(catalogue_book_seq.nextval, ?, ?, ?)",
                book.getBookIsbn().getIsbn(), book.getTitle().getTitle(), book.getAuthor().getName());
        return book;
    }

    /**
     * Persists a new book instance.
     *
     * @param bookInstance the book instance to save.
     * @return the saved book instance.
     */
    BookInstance saveNew(BookInstance bookInstance) {
        jdbcTemplate.update("" +
                        "INSERT INTO catalogue_book_instance " +
                        "(id, isbn, book_id) VALUES " +
                        "(catalogue_book_instance_seq.nextval, ?, ?)",
                bookInstance.getBookIsbn().getIsbn(), bookInstance.getBookId().getBookId());
        return bookInstance;
    }

    /**
     * Finds a book definition by its ISBN.
     *
     * @param isbn the ISBN to search for.
     * @return an {@link Option} containing the book if found, or {@link Option#none()} otherwise.
     */
    Option<Book> findBy(ISBN isbn) {
        try {
            return Option.of(
                    jdbcTemplate.queryForObject(
                            "SELECT b.* FROM catalogue_book b WHERE b.isbn = ?",
                            new BeanPropertyRowMapper<>(BookDatabaseRow.class),
                            isbn.getIsbn())
                            .toBook());
        } catch (EmptyResultDataAccessException e) {
            return Option.none();

        }
    }

}

/**
 * Data Transfer Object for mapping database rows to Book objects.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class BookDatabaseRow {
    String isbn;
    String author;
    String title;

    /**
     * Converts this row data into a Book domain object.
     *
     * @return the reconstructed Book.
     */
    Book toBook() {
        return new Book(isbn, author, title);
    }
}
