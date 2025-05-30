package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.subject.Subject;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    private static final Logger logger = LogsCenter.getLogger(EditCommandParser.class);
    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.info("Parsing edit command with arguments: " + args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(
                        args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_SUBJECT);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
            logger.info("Parsed index: " + index.getOneBased());
        } catch (ParseException pe) {
            logger.warning("Failed to parse index: " + pe.getMessage());
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        parseSubjectsForEdit(argMultimap.getAllValues(PREFIX_SUBJECT)).ifPresent(editPersonDescriptor::setSubjects);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            logger.warning("No fields edited in edit command");
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        logger.info("Successfully created EditCommand with index: " + index.getOneBased());
        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> subjects} into a {@code Set<Tag>} if {@code subjects} is non-empty.
     * If {@code subjects} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero subjects.
     */
    private Optional<Set<Subject>> parseSubjectsForEdit(Collection<String> subjects) throws ParseException {
        assert subjects != null;

        logger.fine("Parsing subjects for edit: " + subjects);

        if (subjects.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> subjectSet = subjects.size() == 1 && subjects.contains("")
                ? Collections.emptySet() : subjects;
        return Optional.of(ParserUtil.parseSubjects(subjectSet));
    }

}
