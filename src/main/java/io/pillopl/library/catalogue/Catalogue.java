package io.pillopl.library.catalogue;

import io.pillopl.library.commons.commands.Result;
import io.pillopl.library.commons.events.DomainEvents;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static io.pillopl.library.commons.commands.Result.Rejection;
import static io.pillopl.library.commons.commands.Result.Success;

/**
 * The main entry point for the Catalogue module.
 * <p>
 * This class acts as a Service or Aggregate Root wrapper for the Catalogue context.
 * It provides functionality to add book definitions and physical book instances to the catalogue.
 * It also handles the publication of domain events (e.g., {@link BookInstanceAddedToCatalogue}).
 * </p>
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Catalogue {

    private final CatalogueDatabase database;
    private final DomainEvents domainEvents;

    /**
     * Adds a new book definition to the catalogue.
     * <p>
     * This method creates a new {@link Book} entity and saves it. This does not create a physical copy
     * (see {@link #addBookInstance(String, BookType)}).
     * </p>
     *
     * @param author the author of the book.
     * @param title  the title of the book.
     * @param isbn   the ISBN of the book.
     * @return a {@link Try} containing {@link Result#Success} if successful, or a failure if validation/persistence fails.
     */
    public Try<Result> addBook(String author, String title, String isbn) {
        return Try.of(() -> {
            Book book = new Book(isbn, author, title);
            database.saveNew(book);
            return Success;
        });
    }

    /**
     * Adds a new physical book instance to the catalogue.
     * <p>
     * This method looks up the book definition by ISBN, creates a new {@link BookInstance},
     * saves it, and publishes a {@link BookInstanceAddedToCatalogue} event.
     * </p>
     *
     * @param isbn     the ISBN of the book definition.
     * @param bookType the type of the book instance (e.g., Restricted, Circulating).
     * @return a {@link Try} containing {@link Result#Success} if the book definition exists and the instance is added,
     *         or {@link Result#Rejection} if the book definition is not found.
     */
    public Try<Result> addBookInstance(String isbn, BookType bookType) {
        return Try.of(() -> database
                .findBy(new ISBN(isbn))
                .map(book -> BookInstance.instanceOf(book, bookType))
                .map(this::saveAndPublishEvent)
                .map(savedInstance -> Success)
                .getOrElse(Rejection));
    }

    private BookInstance saveAndPublishEvent(BookInstance bookInstance) {
        database.saveNew(bookInstance);
        domainEvents.publish(new BookInstanceAddedToCatalogue(bookInstance));
        return bookInstance;
    }


}

