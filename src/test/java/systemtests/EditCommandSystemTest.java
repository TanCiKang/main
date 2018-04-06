package systemtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INDUSTRY_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.INDUSTRY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_INDUSTRY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_LOCATION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ROLE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_SALARY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LOCATION_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.LOCATION_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.SALARY_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.SALARY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INDUSTRY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LOCATION_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SALARY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_INTERNSHIPS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_INTERNSHIP;
import static seedu.address.testutil.TypicalInternships.AMY;
import static seedu.address.testutil.TypicalInternships.BOB;
import static seedu.address.testutil.TypicalInternships.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.internship.Address;
import seedu.address.model.internship.Email;
import seedu.address.model.internship.Industry;
import seedu.address.model.internship.Internship;
import seedu.address.model.internship.Location;
import seedu.address.model.internship.Name;
import seedu.address.model.internship.Role;
import seedu.address.model.internship.Salary;
import seedu.address.model.internship.exceptions.DuplicateInternshipException;
import seedu.address.model.internship.exceptions.InternshipNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.InternshipBuilder;
import seedu.address.testutil.InternshipUtil;

public class EditCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void edit() throws Exception {
        Model model = getModel();

        /* ----------------- Performing edit operation while an unfiltered list is being shown ---------------------- */

        /* Case: edit all fields, command with leading spaces, trailing spaces and multiple spaces between each field
         * -> edited
         */
        Index index = INDEX_FIRST_INTERNSHIP;
        String command = " " + EditCommand.COMMAND_WORD + "  " + index.getOneBased() + "  " + NAME_DESC_BOB + "  "
                + SALARY_DESC_BOB + " " + EMAIL_DESC_BOB + "  " + ADDRESS_DESC_BOB + " " + INDUSTRY_DESC_BOB + "  "
                + LOCATION_DESC_BOB + "  " + ROLE_DESC_BOB + "   " + TAG_DESC_HUSBAND + " ";
        Internship editedInternship = new InternshipBuilder().withName(VALID_NAME_BOB).withSalary(VALID_SALARY_BOB)
                .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB).withIndustry(VALID_INDUSTRY_BOB)
                .withLocation(VALID_LOCATION_BOB).withRole(VALID_ROLE_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertCommandSuccess(command, index, editedInternship);

        /* Case: undo editing the last internship in the list -> last internship restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo editing the last internship in the list -> last internship edited again */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        model.updateInternship(
                getModel().getFilteredInternshipList().get(INDEX_FIRST_INTERNSHIP.getZeroBased()), editedInternship);
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: edit a internship with new values same as existing values -> edited */

        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + SALARY_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + INDUSTRY_DESC_BOB + LOCATION_DESC_BOB + ROLE_DESC_BOB
                + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandSuccess(command, index, BOB);

        /* Case: edit some fields -> edited */
        index = INDEX_FIRST_INTERNSHIP;
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + TAG_DESC_FRIEND;
        Internship internshipToEdit = getModel().getFilteredInternshipList().get(index.getZeroBased());
        editedInternship = new InternshipBuilder(internshipToEdit).withTags(VALID_TAG_FRIEND).build();
        assertCommandSuccess(command, index, editedInternship);

        /* Case: clear tags -> cleared */
        index = INDEX_FIRST_INTERNSHIP;
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + " " + PREFIX_TAG.getPrefix();
        editedInternship = new InternshipBuilder(internshipToEdit).withTags().build();
        assertCommandSuccess(command, index, editedInternship);

        /* ------------------ Performing edit operation while a filtered list is being shown ------------------------ */

        /* Case: filtered internship list, edit index within bounds of address book and internship list -> edited */

        /*
        showInternshipsWithName(KEYWORD_MATCHING_MEIER);
        index = INDEX_FIRST_INTERNSHIP;
        assertTrue(index.getZeroBased() < getModel().getFilteredInternshipList().size());
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + " " + NAME_DESC_BOB;
        internshipToEdit = getModel().getFilteredInternshipList().get(index.getZeroBased());
        editedInternship = new InternshipBuilder(internshipToEdit).withName(VALID_NAME_BOB).build();
        assertCommandSuccess(command, index, editedInternship);
        */

        /* Case: filtered internship list, edit index within bounds of address book but out of bounds of internship list
         * -> rejected
         */
        showInternshipsWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getInternshipList().size();
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + invalidIndex + NAME_DESC_BOB,
                Messages.MESSAGE_INVALID_INTERNSHIP_DISPLAYED_INDEX);

        /* ------------------- Performing edit operation while a internship card is selected ------------------------ */

        /* Case: selects first card in the internship list, edit a internship -> edited, card selection remains
         * unchanged but browser url changes
         */

        showAllInternships();
        index = INDEX_FIRST_INTERNSHIP;
        selectInternship(index);

        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_AMY + SALARY_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY
                + TAG_DESC_FRIEND;
        // this can be misleading: card selection actually remains unchanged but the
        // browser's url is updated to reflect the new internship's name
        assertCommandSuccess(command, index, AMY, index);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid index (0) -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " 0" + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: invalid index (-1) -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " -1" + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: invalid index (size + 1) -> rejected */
        invalidIndex = getModel().getFilteredInternshipList().size() + 1;
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + invalidIndex + NAME_DESC_BOB,
                Messages.MESSAGE_INVALID_INTERNSHIP_DISPLAYED_INDEX);

        /* Case: missing index -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: missing all fields -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased(),
                EditCommand.MESSAGE_NOT_EDITED);

        /* Case: invalid name -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                        + INVALID_NAME_DESC, Name.MESSAGE_NAME_CONSTRAINTS);

        /* Case: invalid phone -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                        + INVALID_SALARY_DESC, Salary.MESSAGE_SALARY_CONSTRAINTS);

        /* Case: invalid email -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                        + INVALID_EMAIL_DESC, Email.MESSAGE_EMAIL_CONSTRAINTS);

        /* Case: invalid address -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                        + INVALID_ADDRESS_DESC, Address.MESSAGE_ADDRESS_CONSTRAINTS);

        /* Case: invalid industry -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                + INVALID_INDUSTRY_DESC, Industry.MESSAGE_INDUSTRY_CONSTRAINTS);

        /* Case: invalid location -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                + INVALID_LOCATION_DESC, Location.MESSAGE_LOCATION_CONSTRAINTS);

        /* Case: invalid role -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                + INVALID_ROLE_DESC, Role.MESSAGE_ROLE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_INTERNSHIP.getOneBased()
                        + INVALID_TAG_DESC, Tag.MESSAGE_TAG_CONSTRAINTS);

        /* Case: edit a internship with new values same as another internship's values -> rejected */
        executeCommand(InternshipUtil.getAddCommand(BOB));
        assertTrue(getModel().getAddressBook().getInternshipList().contains(BOB));
        index = INDEX_FIRST_INTERNSHIP;
        assertFalse(getModel().getFilteredInternshipList().get(index.getZeroBased()).equals(BOB));
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + SALARY_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + INDUSTRY_DESC_BOB + LOCATION_DESC_BOB + ROLE_DESC_BOB
                + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_INTERNSHIP);

        /* Case: edit a internship with new values same as another internship's values but with different tags ->
         * rejected
         */
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + SALARY_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + INDUSTRY_DESC_BOB + LOCATION_DESC_BOB + ROLE_DESC_BOB
                + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_INTERNSHIP);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Index, Internship, Index)} except that
     * the browser url and selected card remain unchanged.
     * @param toEdit the index of the current model's filtered list
     * @see EditCommandSystemTest#assertCommandSuccess(String, Index, Internship, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Internship editedInternship) {
        assertCommandSuccess(command, toEdit, editedInternship, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing {@code EditCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the internship at index {@code toEdit} being
     * updated to values specified {@code editedInternship}.<br>
     * @param toEdit the index of the current model's filtered list.
     * @see EditCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Internship editedInternship,
            Index expectedSelectedCardIndex) {
        Model expectedModel = getModel();
        try {
            expectedModel.updateInternship(
                    expectedModel.getFilteredInternshipList().get(toEdit.getZeroBased()), editedInternship);
            expectedModel.updateFilteredInternshipList(PREDICATE_SHOW_ALL_INTERNSHIPS);
        } catch (DuplicateInternshipException | InternshipNotFoundException e) {
            throw new IllegalArgumentException(
                    "editedInternship is a duplicate in expectedModel, or it isn't found in the model.");
        }

        assertCommandSuccess(command, expectedModel,
                String.format(EditCommand.MESSAGE_EDIT_INTERNSHIP_SUCCESS, editedInternship),
                expectedSelectedCardIndex);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} except that the
     * browser url and selected card remain unchanged.
     * @see EditCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that the browser url and selected card update accordingly depending on the card at
     * {@code expectedSelectedCardIndex}.<br>
     * 5. Asserts that the status bar's sync status changes.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
            Index expectedSelectedCardIndex) {
        executeCommand(command);
        expectedModel.updateFilteredInternshipList(PREDICATE_SHOW_ALL_INTERNSHIPS);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
