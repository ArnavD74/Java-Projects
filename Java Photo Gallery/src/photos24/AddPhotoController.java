package photos24;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for adding photos to an album in the Photos24 application.
 * This class uses the default constructor.
 */
public class AddPhotoController {

    /**
     * Creating the ImageView object
     */
    @FXML
    private ImageView imageView;

    /**
     * Creating Button objects for the left and right arrows
     */
    @FXML
    private Button leftArrowButton, rightArrowButton;

    /**
     * List of selected photos to be added to an album.
     */
    private final List<File> selectedPhotos = new ArrayList<>();

    /**
     * Index of the currently displayed photo in the selected photos list.
     */
    private int currentIndex = 0;

    /**
     * Currently selected album in the application.
     */
    private Album currentAlbum;

    /**
     * Sets the current album to which photos will be added.
     *
     * @param album The album to be set as the current album.
     */
    public void setCurrentAlbum(Album album) {
        this.currentAlbum = album;
    }

    /**
     * Handles the action of choosing photos from the file system.
     * Opens a file chooser dialog to select multiple image files.
     */
    @FXML
    private void handleChoosePhotos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if (files != null) {
            selectedPhotos.addAll(files);
            if (!selectedPhotos.isEmpty()) {
                displayPhoto(0);
                updateButtons();
            }
        }
    }

    /**
     * Displays the photo at the specified index in the imageView.
     *
     * @param index The index of the photo to be displayed.
     */
    private void displayPhoto(int index) {
        if (!selectedPhotos.isEmpty()) {
            Image image = new Image(selectedPhotos.get(index).toURI().toString());
            imageView.setImage(image);
        }
    }

    /**
     * Handles the action to view the previous photo in the list of selected photos.
     * Decrements the current photo index and updates the view.
     */
    @FXML
    private void handlePreviousPhoto() {
        if (!selectedPhotos.isEmpty() && currentIndex > 0) {
            displayPhoto(--currentIndex);
            updateButtons();
        }
    }

    /**
     * Handles the action to view the next photo in the list of selected photos.
     * Increments the current photo index and updates the view.
     */
    @FXML
    private void handleNextPhoto() {
        if (!selectedPhotos.isEmpty() && currentIndex < selectedPhotos.size() - 1) {
            displayPhoto(++currentIndex);
            updateButtons();
        }
    }

    /**
     * Updates the state of navigation buttons based on the current index of the
     * photo being displayed.
     */
    private void updateButtons() {
        leftArrowButton.setDisable(currentIndex <= 0);
        rightArrowButton.setDisable(currentIndex >= selectedPhotos.size() - 1);
    }

    /**
     * Handles the action of adding the selected photos to the current album.
     * Adds existing pictures from the library or new ones from the selected files.
     */
    @FXML
    private void handleAddPhotos() {
        for (File photoFile : selectedPhotos) {
            boolean pictureExists = false;
            Picture existingPicture = null;
            User currentUser = Controller.getCurrentUser();
            for (Album a : currentUser.getAlbums()) {
                for (Picture p : a.getPictures()) {
                    if (p.getName().equals(photoFile.getPath())) {
                        pictureExists = true;
                        existingPicture = p;
                        break;
                    }
                }
                if (pictureExists) {
                    break;
                }
            }
            if (pictureExists && existingPicture != null) {
                if (this.currentAlbum.getPictures().contains(existingPicture)) {
                    new Alert(AlertType.ERROR,
                            "The photo \n" + existingPicture.getName() + "\nalready exists in this album")
                            .showAndWait();
                } else {
                    this.currentAlbum.addPicture(existingPicture);
                    existingPicture.addToAlbum(this.currentAlbum);
                }
            } else {
                Picture newPicture = new Picture(photoFile.getPath());
                this.currentAlbum.addPicture(newPicture);
                newPicture.addToAlbum(this.currentAlbum);
            }
        }
        closeStage();
    }

    /**
     * Closes the current stage (window) after serialization of users.
     * This method is called after adding photos to the album.
     */
    private void closeStage() {
        Photos.serializeUsers();
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
    }
}
