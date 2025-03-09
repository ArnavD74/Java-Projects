package photos24;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing tags in the Photos24 application.
 */
public class TagController {

    /**
     * The VBox used in the Tag controller's layout.
     */
    @FXML
    private VBox mainLayout;

    /**
     * The GridPane used to view the list of each type of tag available.
     */
    @FXML
    private GridPane singularTagGrid, multipleTagGrid;

    /**
     * The Button used to run the save changes method.
     */
    @FXML
    private Button saveChangesButton;

    /**
     * The Picture object whose tags are being changed.
     */
    private Picture currentPicture;

    /**
     * The User object that holds the current picture.
     */
    private User currentUser;

    /**
     * A map of CheckBox objects that correlate to tag pairs and whether the tag is
     * enabled for the current picture or not.
     */
    private Map<CheckBox, Pair<TextField, Tag>> tagFields = new HashMap<>();

    /**
     * Sets the current user and picture.
     * 
     * @param user    The current user.
     * @param picture The current picture.
     */
    public void setCurrentUserAndPicture(User user, Picture picture) {
        this.currentUser = user;
        this.currentPicture = picture;
        populateTags();
    }

    /**
     * Populates the tags in the layout.
     */
    private void populateTags() {
        singularTagGrid.getChildren().clear();
        multipleTagGrid.getChildren().clear();
        tagFields.clear();
        mainLayout.getChildren().removeIf(node -> node instanceof Label
                && mainLayout.getChildren().indexOf(node) > mainLayout.getChildren().indexOf(multipleTagGrid));
        for (Tag tag : currentUser.getUserTags()) {
            CheckBox checkBox = new CheckBox(tag.getName());
            boolean isTagAppliedToPicture = currentPicture.hasTag(tag);
            checkBox.setSelected(isTagAppliedToPicture);
            TextField valueField = new TextField();
            List<String> tagValues = currentPicture.getTagValues(tag);
            if (!tagValues.isEmpty()) {
                String textValue = tag.isMultiple() ? String.join(", ", tagValues) : tagValues.get(0);
                valueField.setText(textValue);
            }
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(event -> handleTagDelete(tag));
            GridPane targetGrid = tag.isMultiple() ? multipleTagGrid : singularTagGrid;
            targetGrid.addRow(targetGrid.getChildren().size(), checkBox, valueField, deleteButton);
            tagFields.put(checkBox, new Pair<>(valueField, tag));
        }
        Label instructionLabel = new Label(
                "Click on the X to permanently delete the tag.\nValues for a 'Multiple' Tag are separated by commas\ne.g. Red, Blue");
        mainLayout.getChildren().add(instructionLabel);
    }

    /**
     * Saves the changes made to the tags.
     */
    @FXML
    private void saveChanges() {
        for (Map.Entry<CheckBox, Pair<TextField, Tag>> entry : tagFields.entrySet()) {
            CheckBox checkBox = entry.getKey();
            TextField valueField = entry.getValue().getKey();
            Tag tag = entry.getValue().getValue();
            ArrayList<String> inputValues = convertStringToArrayList(valueField.getText());
            if (checkBox.isSelected()) {
                if (tag.isMultiple()) {
                    HashSet<String> combinedValues = new HashSet<>(inputValues);
                    currentPicture.updateTag(tag, new ArrayList<>(combinedValues));
                    tag.setValues(new ArrayList<>(combinedValues));
                } else {
                    ArrayList<String> newValue = new ArrayList<>();
                    if (!inputValues.isEmpty()) {
                        newValue.add(inputValues.get(0));
                        currentPicture.updateTag(tag, newValue);
                        tag.setValues(newValue);
                    }
                }
            } else {
                currentPicture.removeTag(tag);
            }
            currentUser.updateTag(tag);
        }
        synchronizeUserTagsWithPicture();
        Photos.serializeUsers();
        Stage stage = (Stage) saveChangesButton.getScene().getWindow();
        stage.close();
        populateTags();
    }

    /**
     * Converts a string to an ArrayList.
     * 
     * @param str The string to convert.
     * @return An ArrayList of strings.
     */
    private ArrayList<String> convertStringToArrayList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(str.trim().split("\\s*,\\s*")));
    }

    /**
     * Handles the deletion of a tag.
     * 
     * @param tag The tag to delete.
     */
    private void handleTagDelete(Tag tag) {
        currentUser.removeUserTag(tag);
        currentPicture.removeTag(tag);
        populateTags();
    }

    /**
     * Handles adding a singular-type tag.
     */
    @FXML
    private void handleAddSingularTag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Singular Tag");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new tag:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tagName -> {
            String trimmedTagName = tagName.trim();
            if (!trimmedTagName.isEmpty() && !tagExists(trimmedTagName)) {
                Tag newTag = new Tag(trimmedTagName, new ArrayList<>(), false);
                currentUser.addTag(newTag);
                populateTags();
            } else if (!trimmedTagName.isEmpty()) {
                showAlert("Duplicate Tag", "A tag with this name already exists.");
            }
        });
    }

    /**
     * Handles adding a multiple-type tag.
     */
    @FXML
    private void handleAddMultipleTag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Multiple Tag");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new tag:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tagName -> {
            String trimmedTagName = tagName.trim();
            if (!trimmedTagName.isEmpty() && !tagExists(trimmedTagName)) {
                Tag newTag = new Tag(trimmedTagName, new ArrayList<>(), true);
                currentUser.addTag(newTag);
                populateTags();
            } else if (!trimmedTagName.isEmpty()) {
                showAlert("Duplicate Tag", "A tag with this name already exists.");
            }
        });
    }

    /**
     * Checks if a tag with a specific name already exists.
     * 
     * @param tagName The name of the tag.
     * @return True if the tag exists, false otherwise.
     */
    private boolean tagExists(String tagName) {
        for (Tag tag : currentUser.getUserTags()) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shows an alert with a given title and content.
     * 
     * @param title   The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Synchronizes tags between the User and the Picture objects.
     */
    private void synchronizeUserTagsWithPicture() {
        for (Tag globalTag : currentUser.getTags()) {
            if (globalTag.isMultiple()) {
                HashSet<String> combinedValues = new HashSet<>();
                for (Picture picture : currentUser.getPictures()) {
                    Tag pictureTag = picture.getTag(globalTag.getName());
                    if (pictureTag != null) {
                        combinedValues.addAll(pictureTag.getValues());
                    }
                }
                globalTag.setValues(new ArrayList<>(combinedValues));
            }
        }
    }
}