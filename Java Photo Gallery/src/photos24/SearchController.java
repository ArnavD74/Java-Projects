package photos24;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller for searching photos in the Photos24 application.
 */
public class SearchController {

    /**
     * The DatePicker used to choose the start date for filtering photos.
     */
    @FXML
    private DatePicker startDatePicker;

    /**
     * The DatePicker used to choose the end date for filtering photos.
     */
    @FXML
    private DatePicker endDatePicker;

    /**
     * The TextField object used to hold and edit the values of a tag.
     */
    @FXML
    private TextField tagTextField;

    /**
     * The TilePane that shows all pictures filtered through the search criteria.
     */
    @FXML
    private TilePane searchResultGrid;

    /**
     * A List of Picture objects that holds all pictures filtered through the search
     * criteria.
     */
    private List<Picture> filteredPhotos;

    /**
     * A List of TagValuePair objects that holds all of the User's tags that can be
     * searched by.
     */
    private List<TagValuePair> tagValuePairs = new ArrayList<>();

    /**
     * Displays an alert with a specified title and message.
     * 
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initiates the search for photos based on the specified criteria.
     */
    @FXML
    private void searchPhotos() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String searchQuery = tagTextField.getText().trim();
        tagValuePairs = parseSearchQuery(searchQuery);
        Calendar startCalendar = null;
        Calendar endCalendar = null;
        if (startDate != null) {
            startCalendar = Calendar.getInstance();
            startCalendar.set(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
        }
        if (endDate != null) {
            endCalendar = Calendar.getInstance();
            endCalendar.set(endDate.getYear(), endDate.getMonthValue() - 1, endDate.getDayOfMonth());
        }

        if (startDate != null || endDate != null || !searchQuery.isEmpty()) {
            Set<Picture> uniquePictures = new HashSet<>();
            for (Picture picture : Controller.getCurrentUser().getPictures()) {
                if (isInDateRange(picture.getDateLastModified(), startCalendar, endCalendar)
                        && (tagValuePairs.isEmpty() || evaluateTagsWithOperator(picture, tagValuePairs))) {
                    uniquePictures.add(picture);
                }
            }

            filteredPhotos = new ArrayList<>(uniquePictures);

            if (filteredPhotos.isEmpty()) {
                showAlert("No Photos Found", "No photos match your search criteria.");
            } else {
                displaySearchResults();
            }
        } else {
            searchResultGrid.getChildren().clear();
            showAlert("Search Criteria Missing", "Please enter search criteria.");
        }
    }

    /**
     * Evaluates if a picture satisfies the search criteria based on tags and
     * operators.
     * 
     * @param picture The picture to evaluate.
     * @param pairs   The list of TagValuePair that represents search criteria.
     * @return True if the picture satisfies the criteria, false otherwise.
     */
    private boolean evaluateTagsWithOperator(Picture picture, List<TagValuePair> pairs) {
        boolean result = false;
        boolean firstCondition = true;
        for (TagValuePair pair : pairs) {
            if (pair.getTagName() == null || pair.getTagValue() == null) {
                continue;
            }
            Tag tag = picture.getTag(pair.getTagName());
            boolean hasTag = (tag != null) && tag.getValues().contains(pair.getTagValue());
            System.out.println(
                    "Evaluating tag: " + pair.getTagName() + "=" + pair.getTagValue() + ", has tag: " + hasTag);
            if (pair.getOperator() == TagValuePair.Operator.OR) {
                result |= hasTag;
            } else if (pair.getOperator() == TagValuePair.Operator.AND) {
                if (firstCondition) {
                    result = hasTag;
                    firstCondition = false;
                } else {
                    result &= hasTag;
                }
            }
        }
        return result;
    }

    /**
     * Parses the search query into a list of TagValuePairs.
     * 
     * @param query The search query string.
     * @return A list of TagValuePairs representing the search criteria.
     */
    private List<TagValuePair> parseSearchQuery(String query) {
        List<TagValuePair> pairs = new ArrayList<>();
        if (query.isEmpty()) {
            return pairs;
        }
        String[] andTerms = query.split(" AND ");
        for (String andTerm : andTerms) {
            String[] orTerms = andTerm.split(" OR ");
            for (int i = 0; i < orTerms.length; i++) {
                String[] parts = orTerms[i].split("=|, ?");
                if (parts.length == 2) {
                    String tagName = parts[0].trim();
                    String tagValue = parts[1].trim();
                    TagValuePair.Operator operator = andTerms.length > 1 ? TagValuePair.Operator.AND
                            : TagValuePair.Operator.OR;
                    pairs.add(new TagValuePair(tagName, tagValue, operator));
                    System.out.println("Adding TagValuePair - Tag: " + tagName + ", Value: " + tagValue + ", Operator: "
                            + operator);
                } else {
                }
            }
        }
        return pairs;
    }

    /**
     * Checks if a picture's date is within the specified date range.
     * 
     * @param pictureDate The date of the picture.
     * @param startDate   The start date of the range.
     * @param endDate     The end date of the range.
     * @return True if the picture's date is within the range, false otherwise.
     */
    private boolean isInDateRange(Calendar pictureDate, Calendar startDate, Calendar endDate) {
        if (startDate != null && pictureDate.before(startDate)) {
            return false;
        }
        if (endDate != null && pictureDate.after(endDate)) {
            return false;
        }
        return true;
    }

    /**
     * Displays the search results in the searchResultGrid.
     */
    private void displaySearchResults() {
        searchResultGrid.getChildren().clear();
        for (Picture picture : filteredPhotos) {
            ImageView imageView = new ImageView(new Image("file:" + picture.getName()));
            imageView.setFitHeight(120);
            imageView.setFitWidth(120);
            imageView.setPreserveRatio(true);
            StackPane imageContainer = new StackPane(imageView);
            imageContainer.setPadding(new Insets(10));
            searchResultGrid.getChildren().add(imageContainer);
        }
    }

    /**
     * Saves the search results to a new album.
     */
    @FXML
    private void saveToNewAlbum() {
        if (filteredPhotos == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Cannot save an album with no photos.");
            alert.showAndWait();
            return;
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        TextField albumNameField = new TextField();
        albumNameField.setPromptText("Enter album name");
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String albumName = albumNameField.getText();
            if (!albumName.isEmpty()) {
                Album newAlbum = new Album(albumName);
                newAlbum.getPictures().addAll(filteredPhotos);
                Controller.getCurrentUser().getAlbums().add(newAlbum);
                Photos.serializeUsers();
                stage.close();
            }
        });
        VBox layout = new VBox(10, albumNameField, saveButton);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Save to New Album");
        stage.showAndWait();
    }
}