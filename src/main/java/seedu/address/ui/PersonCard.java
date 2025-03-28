package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane nextLesson;
    @FXML
    private Label remark;
    @FXML
    private Label subjects;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        remark.setText(person.getRemark().value);
        email.setText(person.getEmail().value);

        if (person.getNextLesson() != null && !person.getNextLesson().toString().isEmpty()) {
            Label nextLessonLabel = new Label(person.getNextLesson().toString());
            nextLessonLabel.getStyleClass().add("label");
            nextLesson.getChildren().add(nextLessonLabel);
        } else {
            nextLesson.getChildren().clear(); // Clear if no next lesson
        }

        StringBuilder subjectLabel = new StringBuilder();
        person.getSubjects().stream()
                .sorted(Comparator.comparing(subject -> subject.subjectName))
                .forEach(subject ->
                        subjectLabel.append(subject.subjectName).append(" | "));
        if (subjectLabel.length() > 0) {
            subjects.setText(subjectLabel.substring(0, subjectLabel.length() - 3));
        } else {
            subjects.setText("");
        }

        if (!person.getRemark().value.isEmpty()) {
            remark.setText(person.getRemark().value);
            remark.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            remark.setText("NOT PAID");
            remark.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }
}
