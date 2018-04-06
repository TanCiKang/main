package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
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
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INDUSTRY_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INDUSTRY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LOCATION_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LOCATION_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SALARY_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SALARY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.TypicalInternships.ALICE;
import static seedu.address.testutil.TypicalInternships.AMY;
import static seedu.address.testutil.TypicalInternships.BOB;
import static seedu.address.testutil.TypicalInternships.CARL;
import static seedu.address.testutil.TypicalInternships.HOON;
import static seedu.address.testutil.TypicalInternships.IDA;
import static seedu.address.testutil.TypicalInternships.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.internship.Address;
import seedu.address.model.internship.Email;
import seedu.address.model.internship.Industry;
import seedu.address.model.internship.Internship;
import seedu.address.model.internship.Name;
import seedu.address.model.internship.Role;
import seedu.address.model.internship.Salary;
import seedu.address.model.internship.exceptions.DuplicateInternshipException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.InternshipBuilder;
import seedu.address.testutil.InternshipUtil;

public class AddCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void add() throws Exception {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a internship without tags to a non-empty address book, command with leading spaces and trailing
         * spaces -> added
         */
        Internship toAdd = AMY;
        String command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "  " + SALARY_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   " + ADDRESS_DESC_AMY + "   " + INDUSTRY_DESC_AMY + "   " + LOCATION_DESC_AMY
                + "   " + ROLE_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
        assertCommandSuccess(command, toAdd);

        /* Case: undo adding Amy to the list -> Amy deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Amy to the list -> Amy added again */
        command = RedoCommand.COMMAND_WORD;
        model.addInternship(toAdd);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a internship with all fields same as another internship in the address book except name -> added */
        toAdd = new InternshipBuilder().withName(VALID_NAME_BOB).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_BOB + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except salary ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_BOB).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_BOB + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except email ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_BOB + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except address ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_BOB).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_BOB
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except industry ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_BOB).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_BOB + LOCATION_DESC_AMY + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except location ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_BOB)
                .withRole(VALID_ROLE_AMY).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_BOB + ROLE_DESC_AMY + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship with all fields same as another internship in the address book except role ->
         * added
         */
        toAdd = new InternshipBuilder().withName(VALID_NAME_AMY).withSalary(VALID_SALARY_AMY).withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY).withIndustry(VALID_INDUSTRY_AMY).withLocation(VALID_LOCATION_AMY)
                .withRole(VALID_ROLE_BOB).withTags(VALID_TAG_FRIEND).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_BOB + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add to empty address book -> added */
        deleteAllInternships();
        assertCommandSuccess(ALICE);

        /* Case: add a internship with tags, command with parameters in random order -> added */
        toAdd = BOB;
        command = AddCommand.COMMAND_WORD + TAG_DESC_FRIEND + SALARY_DESC_BOB + LOCATION_DESC_BOB + ADDRESS_DESC_BOB
                + NAME_DESC_BOB + TAG_DESC_HUSBAND + EMAIL_DESC_BOB + INDUSTRY_DESC_BOB + ROLE_DESC_BOB;
        assertCommandSuccess(command, toAdd);

        /* Case: add a internship, missing tags -> added */
        assertCommandSuccess(HOON);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */

        /* Case: filters the internship list before adding -> added */
        //showInternshipsWithName(KEYWORD_MATCHING_MEIER);
        //assertCommandSuccess(IDA);

        /* ------------------------ Perform add operation while a internship card is selected ----------------------- */

        /* Case: selects first card in the internship list, add a internship -> added, card selection remains
         * unchanged
         */
        selectInternship(Index.fromOneBased(1));
        assertCommandSuccess(CARL);

        /* ----------------------------------- Perform invalid add operations --------------------------------------- */

        /* Case: add a duplicate internship -> rejected */
        command = InternshipUtil.getAddCommand(HOON);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_INTERNSHIP);

        /* Case: add a duplicate internship except with different tags -> rejected */
        // "friends" is an existing tag used in the default model, see TypicalInternships#ALICE
        // This test will fail if a new tag that is not in the model is used, see the bug documented in
        // AddressBook#addInternship(Internship)
        command = InternshipUtil.getAddCommand(HOON) + " " + PREFIX_TAG.getPrefix() + "friends";
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_INTERNSHIP);

        /* Case: missing name -> rejected */
        command = AddCommand.COMMAND_WORD + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + INDUSTRY_DESC_AMY
                + LOCATION_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: missing salary -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + INDUSTRY_DESC_AMY
                + LOCATION_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: missing email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + ADDRESS_DESC_AMY + INDUSTRY_DESC_AMY
                + LOCATION_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: missing address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + INDUSTRY_DESC_AMY
                + LOCATION_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: missing industry -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + LOCATION_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: missing location -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_UNKNOWN_COMMAND, AddCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + InternshipUtil.getInternshipDetails(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = AddCommand.COMMAND_WORD + INVALID_NAME_DESC + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY;
        assertCommandFailure(command, Name.MESSAGE_NAME_CONSTRAINTS);

        /* Case: invalid salary -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + INVALID_SALARY_DESC + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY;
        assertCommandFailure(command, Salary.MESSAGE_SALARY_CONSTRAINTS);

        /* Case: invalid email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + INVALID_EMAIL_DESC + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY;
        assertCommandFailure(command, Email.MESSAGE_EMAIL_CONSTRAINTS);

        /* Case: invalid address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + INVALID_ADDRESS_DESC
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY;
        assertCommandFailure(command, Address.MESSAGE_ADDRESS_CONSTRAINTS);

        /* Case: invalid industry -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INVALID_INDUSTRY_DESC + LOCATION_DESC_AMY + ROLE_DESC_AMY;
        assertCommandFailure(command, Industry.MESSAGE_INDUSTRY_CONSTRAINTS);

        /* Case: invalid location -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + INVALID_LOCATION_DESC + ROLE_DESC_AMY;
        assertCommandFailure(command, Industry.MESSAGE_INDUSTRY_CONSTRAINTS);

        /* Case: invalid role -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + INVALID_ROLE_DESC;
        assertCommandFailure(command, Role.MESSAGE_ROLE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + SALARY_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INDUSTRY_DESC_AMY + LOCATION_DESC_AMY + ROLE_DESC_AMY + INVALID_TAG_DESC;
        assertCommandFailure(command, Tag.MESSAGE_TAG_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Model}, {@code Storage} and {@code InternshipListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Internship toAdd) {
        assertCommandSuccess(InternshipUtil.getAddCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Internship)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(Internship)
     */
    private void assertCommandSuccess(String command, Internship toAdd) {
        Model expectedModel = getModel();
        try {
            expectedModel.addInternship(toAdd);
        } catch (DuplicateInternshipException dpe) {
            throw new IllegalArgumentException("toAdd already exists in the model.");
        }
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Internship)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Model}, {@code Storage} and {@code InternshipListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see AddCommandSystemTest#assertCommandSuccess(String, Internship)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Model}, {@code Storage} and {@code InternshipListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
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
