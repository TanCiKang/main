package seedu.address.testutil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.internship.Internship;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * A utility class to help with building Person objects.
 */
public class SavedPersonBuilder {


    public static final String MESSAGE_DUPLICATE_TAG = "This internship has been saved";
    public final String savedTagName = "saved";

    /**
     * Initializes the PersonBuilder with the data of {@code internshipToCopy}.
     */
    public Internship addTag(Internship internshipToCopy) throws CommandException {
        final UniqueTagList personTags = new UniqueTagList(internshipToCopy.getTags());
        try {
            personTags.add(new Tag(savedTagName));
        } catch (UniqueTagList.DuplicateTagException e) {
            throw new CommandException(MESSAGE_DUPLICATE_TAG);
        }

        // Create map with values = tag object references in the master list
        // used for checking person tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        personTags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of person tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        personTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));

<<<<<<< HEAD
        return new Internship(
                internshipToCopy.getName(),
                internshipToCopy.getSalary(),
                internshipToCopy.getEmail(),
                internshipToCopy.getAddress(),
=======
        return new Person(
                personToCopy.getName(),
                personToCopy.getSalary(),
                personToCopy.getEmail(),
                personToCopy.getAddress(),
>>>>>>> fdd0cc6349183cf8986b03133ee6918870419952
                correctTagReferences);


    }

}
