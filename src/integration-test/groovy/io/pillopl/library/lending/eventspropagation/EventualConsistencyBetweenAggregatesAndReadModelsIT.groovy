package io.pillopl.library.lending.eventspropagation

import io.pillopl.library.common.events.publisher.DomainEventsTestConfig
import io.pillopl.library.lending.LendingTestContext
import io.pillopl.library.lending.book.model.AvailableBook
import io.pillopl.library.lending.book.model.BookFixture
import io.pillopl.library.lending.book.model.BookOnHold
import io.pillopl.library.lending.book.model.BookRepository
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId
import io.pillopl.library.lending.patron.model.HoldDuration
import io.pillopl.library.lending.patron.model.Patron
import io.pillopl.library.lending.patron.model.Patrons
import io.pillopl.library.lending.patron.model.PatronId
import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.sql.DataSource

import static io.pillopl.library.lending.librarybranch.model.LibraryBranchFixture.anyBranch
import static io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHold.bookPlacedOnHoldNow
import static io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHoldEvents
import static io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHoldEvents.events
import static io.pillopl.library.lending.patron.model.PatronEvent.PatronCreated
import static io.pillopl.library.lending.patron.model.PatronFixture.anyPatronId
import static io.pillopl.library.lending.patron.model.PatronFixture.regularPatron
import static io.pillopl.library.lending.patron.model.PatronType.Regular

@SpringBootTest(classes = [LendingTestContext.class, DomainEventsTestConfig.class])
/**
 * Integration test verifying eventual consistency between Aggregates and Read Models.
 * <p>
 * This test ensures that events emitted by the Write Model (Aggregates like {@link Patron} and {@link io.pillopl.library.lending.book.model.Book})
 * are correctly propagated to and reflected in the Read Models (such as {@link io.pillopl.library.lending.dailysheet.model.DailySheet}).
 * It simulates a scenario where a book is placed on hold and verifies the system's state across different components.
 * </p>
 *
 * Key verifications:
 * <ul>
 *     <li>Patron aggregate state update (persisted in database).</li>
 *     <li>Book aggregate reaction to events (status change to {@link BookOnHold}).</li>
 *     <li>Read model update (Daily Sheet).</li>
 * </ul>
 */
class EventualConsistencyBetweenAggregatesAndReadModelsIT extends Specification {

    /**
     * Test fixture: Patron ID used in the test scenario.
     */
    PatronId patronId = anyPatronId()

    /**
     * Test fixture: Library Branch ID used in the test scenario.
     */
    LibraryBranchId libraryBranchId = anyBranch()

    /**
     * Test fixture: An available book to be placed on hold.
     */
    AvailableBook book = BookFixture.circulatingBook()

    @Autowired
    Patrons patronRepo

    @Autowired
    BookRepository bookRepository

    @Autowired
    DataSource datasource

    /**
     * specific condition for asynchronous assertions (eventual consistency).
     * Defaults to a 6-second timeout.
     */
    PollingConditions pollingConditions = new PollingConditions(timeout: 6)

    /**
     * Verifies that the Patron, Book, and DailySheet are synchronized via domain events.
     * <p>
     * Scenario:
     * 1. A book and a patron exist.
     * 2. The patron places the book on hold (event publication).
     * 3. Assertions check if:
     *    - The patron has the hold in the database.
     *    - The book status changes to {@link BookOnHold} (asynchronously).
     *    - The daily sheet read model is updated (asynchronously).
     * </p>
     */
    def 'should synchronize Patron, Book and DailySheet with events'() {
        given:
            bookRepository.save(book)
        and:
            patronRepo.publish(patronCreated())
        when:
            patronRepo.publish(placedOnHold(book))
        then:
            patronShouldBeFoundInDatabaseWithOneBookOnHold(patronId)
        and:
            bookReactedToPlacedOnHoldEvent()
        and:
            dailySheetIsUpdated()
    }

    /**
     * Asserts that the Book aggregate eventually reflects the 'Placed On Hold' event.
     * <p>
     * Uses polling to wait for the asynchronous event processing to complete.
     * </p>
     */
    void bookReactedToPlacedOnHoldEvent() {
        pollingConditions.eventually {
            assert bookRepository.findBy(book.bookId).get() instanceof BookOnHold
        }
    }

    /**
     * Asserts that the Daily Sheet read model is eventually updated.
     * <p>
     * Checks the 'holds_sheet' database table for the expected count of holds.
     * Uses polling to wait for the asynchronous projection update.
     * </p>
     */
    void dailySheetIsUpdated() {
        pollingConditions.eventually {
            assert countOfHoldsInDailySheet() == 1
        }
    }

    /**
     * Helper method to count holds in the 'holds_sheet' table for the test patron.
     *
     * @return The count of holds found in the read model database.
     */
    private Object countOfHoldsInDailySheet() {
        return new JdbcTemplate(datasource).query("select count(*) from holds_sheet s where s.hold_by_patron_id = ?",
                [patronId.patronId] as Object[],
                new ColumnMapRowMapper()).get(0)
                .get("COUNT(*)")
    }

    /**
     * Factory method for creating {@link BookPlacedOnHoldEvents}.
     *
     * @param book The available book being placed on hold.
     * @return A container with the {@link io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHold} event.
     */
    BookPlacedOnHoldEvents placedOnHold(AvailableBook book) {
        return events(bookPlacedOnHoldNow(
                book.getBookId(),
                book.type(),
                book.libraryBranch,
                patronId,
                HoldDuration.closeEnded(5)))
    }

    /**
     * Factory method for creating a {@link PatronCreated} event.
     *
     * @return A {@link PatronCreated} event for a regular patron.
     */
    PatronCreated patronCreated() {
        return PatronCreated.now(patronId, Regular)
    }

    /**
     * Asserts that the Patron aggregate is persisted in the database with exactly one active hold.
     *
     * @param patronId The ID of the patron to check.
     */
    void patronShouldBeFoundInDatabaseWithOneBookOnHold(PatronId patronId) {
        Patron patron = loadPersistedPatron(patronId)
        assert patron.numberOfHolds() == 1
        assert patron.equals(regularPatron(patronId))
    }


    /**
     * Loads a persisted Patron from the repository.
     *
     * @param patronId The ID of the patron to load.
     * @return The loaded {@link Patron} entity.
     * @throws IllegalStateException if the patron is not found in the repository.
     */
    Patron loadPersistedPatron(PatronId patronId) {
        Option<Patron> loaded = patronRepo.findBy(patronId)
        Patron patron = loaded.getOrElseThrow({
            new IllegalStateException("should have been persisted")
        })
        return patron
    }
}
