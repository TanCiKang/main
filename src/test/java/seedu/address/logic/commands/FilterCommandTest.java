package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.TypicalInternships.CARL;
import static seedu.address.testutil.TypicalInternships.CARL2;
import static seedu.address.testutil.TypicalInternships.DANIEL;
import static seedu.address.testutil.TypicalInternships.GEORGE;
import static seedu.address.testutil.TypicalInternships.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.internship.Internship;
import seedu.address.model.internship.InternshipContainsAllKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterCommand}.
 */
public class FilterCommandTest {

    public static final String MESSAGE_FILTER_RESPONSE = "How would you to sort your results by? You may sort by "
            + "name industry role etc \nE.g sortby industry role industry";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        InternshipContainsAllKeywordsPredicate firstPredicate =
                new InternshipContainsAllKeywordsPredicate(Collections.singletonList("first"));
        InternshipContainsAllKeywordsPredicate secondPredicate =
                new InternshipContainsAllKeywordsPredicate(Collections.singletonList("second"));

        FilterCommand filterFirstCommand = new FilterCommand(firstPredicate);
        FilterCommand filterSecondCommand = new FilterCommand(secondPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same values -> return true
        FilterCommand filterFirstCommandCopy = new FilterCommand(firstPredicate);
        assertTrue(filterFirstCommand.equals(filterFirstCommandCopy));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
    }

    @Test
    public void execute_multipleKeywords_zeroInternshipsFound() {
        String expectedMessage = String.format(MESSAGE_FILTER_RESPONSE);
        FilterCommand command = prepareCommand("Carl Daniel Meier");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    public void execute_multipleKeywords_singleInternshipsFound() {
        String expectedMessage = String.format(MESSAGE_FILTER_RESPONSE);
        FilterCommand command = prepareCommand("Kurz Carl");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL));
    }

    @Test
    public void execute_singleKeyword_multipleInternshipsFound() {
        String expectedMessage = String.format(MESSAGE_FILTER_RESPONSE);
        FilterCommand command = prepareCommand("Carl");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, CARL2));
    }

    @Test
    public void execute_singleKeywordNonNameAttribute_multipleInternshipsFound() {
        String expectedMessage = String.format(MESSAGE_FILTER_RESPONSE);
        FilterCommand command = prepareCommand("Street");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, DANIEL, GEORGE, CARL2));
    }


    /**
     * Parses {@code userInput} into a {@code FilterCommand}.
     */
    private FilterCommand prepareCommand(String userInput) {
        FilterCommand command =
                new FilterCommand(new InternshipContainsAllKeywordsPredicate(Arrays.asList(userInput.split("\\s+"))));
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Asserts that {@code command} is successfully executed, and<br>
     *     - the command feedback is equal to {@code expectedMessage}<br>
     *     - the {@code FilteredList<Person>} is equal to {@code expectedList}<br>
     *     - the {@code AddressBook} in model remains the same after executing the {@code command}
     */
    private void assertCommandSuccess(FilterCommand command, String expectedMessage, List<Internship> expectedList) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute();

        assertEquals(expectedMessage, commandResult.feedbackToUser);
        assertEquals(expectedList, model.getFilteredInternshipList());
        assertEquals(expectedAddressBook, model.getAddressBook());
    }
}
