package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_INTERNSHIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.internship.Internship;
import seedu.address.model.internship.exceptions.DuplicateInternshipException;
import seedu.address.model.internship.exceptions.InternshipNotFoundException;
import seedu.address.model.internship.exceptions.SavedTagNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;
import seedu.address.model.util.Sorter;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private static List<String> filterKeywords;
    private final AddressBook addressBook;
    private final FilteredList<Internship> searchedInternships;
    private final FilteredList<Internship> filteredInternships;
    private final SortedList<Internship> sortedFilteredInternships;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.searchedInternships = new FilteredList<>(this.addressBook.getInternshipList());
        this.filteredInternships = new FilteredList<>(searchedInternships);
        this.sortedFilteredInternships = new SortedList<>(filteredInternships);
        filterKeywords = new ArrayList<>();
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    @Override
    public synchronized void deleteInternship(Internship target) throws InternshipNotFoundException {
        addressBook.removeInternship(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addInternship(Internship internship) throws DuplicateInternshipException {
        addressBook.addInternship(internship);
        updateFilteredInternshipList(PREDICATE_SHOW_ALL_INTERNSHIPS);
        indicateAddressBookChanged();
    }

    @Override
    public void updateInternship(Internship target, Internship editedInternship)
            throws DuplicateInternshipException, InternshipNotFoundException {
        requireAllNonNull(target, editedInternship);

        addressBook.updateInternship(target, editedInternship);
        indicateAddressBookChanged();
    }

    //@@author niloc94
    @Override
    public void setComparator(List<String> keywords) {
        Comparator<Internship> comparatorToSet = Sorter.makeComparator(keywords);
        sortedFilteredInternships.setComparator(comparatorToSet);
    }

    public static void setKeywords(List<String> keywords) {
        filterKeywords = keywords;
    }

    public static List<String> getKeywords() {
        return filterKeywords;
    }

    //=========== Add / Remove Tags Methods =============================================================

    //@@author TanCiKang
    /**
     * Add keyword tags that matches the individual internship to the internship
     * @param keyword
     * @param internship
     * @return Internship
     * @throws CommandException
     */
    private static Internship addTagsToInternshipWithMatch(String keyword, Internship internship)
            throws CommandException {
        final UniqueTagList internshipTags = new UniqueTagList(internship.getTags());

        try {
            internshipTags.add(new Tag(keyword));
        } catch (UniqueTagList.DuplicateTagException e) {
            throw new CommandException ("Operation would result in duplicate tags");
        }

        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        internshipTags.forEach(tag -> masterTagObjects.put(tag, tag));

        final Set<Tag> correctTagReferences = new HashSet<>();
        internshipTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));

        return new Internship(
                internship.getName(), internship.getSalary(), internship.getEmail(), internship.getAddress(),
                internship.getIndustry(), internship.getLocation(), internship.getRole(), correctTagReferences);
    }

    //@@author TanCiKang
    /**
     * Add keyword tags that matches the internship to the list of internships in filteredInternships
     * @param filterKeywords
     * @param filteredInternships
     * @param model
     * @throws CommandException
     */
    public static void addTagsToFilteredList (List<String> filterKeywords,
                                              ObservableList<Internship> filteredInternships, Model model)
            throws CommandException {

        for (String keywords : filterKeywords) {
            for (Internship filteredInternship : filteredInternships) {
                if (StringUtil.containsWordIgnoreCase(filteredInternship.toString(), keywords)) {
                    try {
                        model.updateInternship(filteredInternship,
                                addTagsToInternshipWithMatch(keywords, filteredInternship));
                    } catch (DuplicateInternshipException e) {
                        throw new CommandException(MESSAGE_DUPLICATE_INTERNSHIP);
                    } catch (InternshipNotFoundException e) {
                        throw new AssertionError("The target internship cannot be missing");
                    }
                }
            }
        }
        return;
    }

    //@@author TanCiKang
    /**
     * Remove all tags from individual internship other than 'saved' tags
     * @param tagsToBeRemoved
     * @param internship
     * @return
     */
    private static Internship removeTagsFromInternship(Set<Tag> tagsToBeRemoved, Internship internship, Model model) {
        final UniqueTagList internshipTags = new UniqueTagList(internship.getTags());

        for (Tag tagToBeRemoved : tagsToBeRemoved) {
            if (!tagToBeRemoved.toString().equals("[saved]")) {
                try {
                    internshipTags.delete(tagToBeRemoved);
                } catch (SavedTagNotFoundException e) {
                    System.out.println("Saved tag not found!");
                }
            }
        }

        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        internshipTags.forEach(tag -> masterTagObjects.put(tag, tag));

        final Set<Tag> correctTagReferences = new HashSet<>();
        internshipTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));

        return new Internship(
                internship.getName(), internship.getSalary(), internship.getEmail(), internship.getAddress(),
                internship.getIndustry(), internship.getLocation(), internship.getRole(), correctTagReferences);
    }

    //@@author TanCiKang
    /**
     * Remove all tags that are not 'saved' from the internships
     * @param internships
     * @param model
     * @throws CommandException
     */
    public static void removeTagsFromInternshipList(ObservableList<Internship> internships, Model model)
            throws CommandException {

        for (Internship internship : internships) {
            try {
                model.updateInternship(internship, removeTagsFromInternship(internship.getTags(), internship, model));
            } catch (DuplicateInternshipException e) {
                throw new CommandException(MESSAGE_DUPLICATE_INTERNSHIP);
            } catch (InternshipNotFoundException e) {
                throw new AssertionError("The target internship cannot be missing");
            }
        }
        return;
    }

    //=========== Filtered Internship List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Internship} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Internship> getFilteredInternshipList() {
        return FXCollections.unmodifiableObservableList(sortedFilteredInternships);
    }

    @Override
    public void updateFilteredInternshipList(Predicate<Internship> predicate) {
        requireNonNull(predicate);
        filteredInternships.setPredicate(predicate);
    }

    @Override
    public void updateSearchedInternshipList(Predicate<Internship> predicate) {
        requireNonNull(predicate);
        searchedInternships.setPredicate(predicate);
        filteredInternships.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredInternships.equals(other.filteredInternships);
    }
}
