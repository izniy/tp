package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXTLESSON;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.NextLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NextLesson;

/**
 * Parses input arguments and creates a new NextLessonCommand object
 */
public class NextLessonCommandParser implements Parser<NextLessonCommand> {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid date or time format. Expected: 'd/M/yyyy HHmm-HHmm' (e.g., 15/4/2025 0900-1100)";
    public static final String MESSAGE_INVALID_TIME =
            "Time must be between 00:00 and 23:59.";
    public static final String MESSAGE_INVALID_DATE_TIME =
            "Invalid date or time entered. Please ensure you have entered a valid date or time.";
    public static final String MESSAGE_INVALID_START_BEFORE_END = "Start time must be before end time.";
    public static final String MESSAGE_INVALID_START_END_TIME = "Start and end time cannot be the same.";
    public static final String MESSAGE_INVALID_PAST_LESSON = "Lesson cannot be in the past.";
    public static final String MESSAGE_INVALID_YEAR = "Lesson must be less than 1 year from now. ";

    private static final String DATE_REGEX = "(\\d{1,2})/(\\d{1,2})/(\\d{4})\\s(\\d{4})-(\\d{4})";
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalTime CURRENT_TIME = LocalTime.now();

    /**
     * Parses the given {@code String} of arguments in the context of the {@code NextLessonCommand}
     * and returns a {@code NextLessonCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NextLessonCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NEXTLESSON);

        Index index;
        try {
            index = ParserUtil.parseIndex(argumentMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NextLessonCommand.MESSAGE_USAGE), ive);
        }

        if (!argumentMultimap.getValue(PREFIX_NEXTLESSON).isPresent()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NextLessonCommand.MESSAGE_USAGE));
        }

        String dateTimeString = argumentMultimap.getValue(PREFIX_NEXTLESSON).orElse("");

        if (dateTimeString.isEmpty()) {
            return new NextLessonCommand(index, new NextLesson());
        }

        return new NextLessonCommand(index, parseNextLesson(dateTimeString));
    }

    private NextLesson parseNextLesson(String dateTimeString) throws ParseException {
        Pattern pattern = Pattern.compile(DATE_REGEX);
        Matcher matcher = pattern.matcher(dateTimeString);

        if (!matcher.matches()) {
            throw new ParseException(MESSAGE_CONSTRAINTS);
        }

        try {
            // Parse date components
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));

            // Parse start time
            String startTimeStr = matcher.group(4);
            int startHours = Integer.parseInt(startTimeStr.substring(0, 2));
            int startMinutes = Integer.parseInt(startTimeStr.substring(2, 4));

            // Parse end time
            String endTimeStr = matcher.group(5);
            int endHours = Integer.parseInt(endTimeStr.substring(0, 2));
            int endMinutes = Integer.parseInt(endTimeStr.substring(2, 4));

            // Validate time ranges
            if (startHours > 23 || startMinutes > 59 || endHours > 23 || endMinutes > 59) {
                throw new ParseException(MESSAGE_INVALID_TIME);
            }

            LocalDate date = LocalDate.of(year, month, day);
            LocalTime startTime = LocalTime.of(startHours, startMinutes);
            LocalTime endTime = LocalTime.of(endHours, endMinutes);

            if (!date.isBefore(CURRENT_DATE.plusYears(1))) {
                throw new ParseException(MESSAGE_INVALID_YEAR, new IllegalArgumentException());
            }

            if (startTime.equals(endTime)) {
                throw new ParseException(MESSAGE_INVALID_START_END_TIME, new IllegalArgumentException());
            }

            if (!startTime.isBefore(endTime)) {
                throw new ParseException(MESSAGE_INVALID_START_BEFORE_END, new IllegalArgumentException());
            }

            if (date.isEqual(CURRENT_DATE) && startTime.isBefore(CURRENT_TIME)) {
                throw new ParseException(MESSAGE_INVALID_PAST_LESSON, new IllegalArgumentException());
            }

            if (date.isBefore(CURRENT_DATE)) {
                throw new ParseException(MESSAGE_INVALID_PAST_LESSON, new IllegalArgumentException());
            }

            return new NextLesson(date, startTime, endTime);
        } catch (NumberFormatException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NextLessonCommand.MESSAGE_USAGE), e);
        } catch (DateTimeException e) {
            throw new ParseException(MESSAGE_INVALID_DATE_TIME, e);
        }
    }
}
