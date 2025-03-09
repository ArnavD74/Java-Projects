package photos24;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for creating new albums in the Photos24 application.
 */
public class CreateAlbumController implements Serializable {

    /**
     * Takes in the album name from the user
     */
    @FXML
    private TextField albumNameField;

    /**
     * Display the preview of selected photos
     */
    @FXML
    private ImageView imageView;

    /**
     * List of photos
     */
    private final List<File> photos = new ArrayList<>();

    /**
     * The index of the current photo
     */
    private int currentIndex = 0;

    /**
     * The current UserController object that can be retrieved by other classes that
     * handle renaming albums.
     */
    private UserController userController;

    /**
     * Sets the UserController.
     * 
     * @param userController The user controller to be set.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    /**
     * Handles the upload of photos for the new album.
     */
    @FXML
    private void handleUploadPhotos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            photos.addAll(selectedFiles);
            if (!photos.isEmpty()) {
                displayImage(0);
            }
        }
    }

    /**
     * Handles the action of viewing the previous image in the photo list.
     */
    @FXML
    private void handlePreviousImage() {
        if (!photos.isEmpty() && currentIndex > 0) {
            displayImage(--currentIndex);
        }
    }

    /**
     * Handles the action of viewing the next image in the photo list.
     */
    @FXML
    private void handleNextImage() {
        if (!photos.isEmpty() && currentIndex < photos.size() - 1) {
            displayImage(++currentIndex);
        }
    }

    /**
     * Displays the image at the given index.
     * 
     * @param index The index of the image to display.
     */
    private void displayImage(int index) {
        Image image = new Image(photos.get(index).toURI().toString());
        imageView.setImage(image);
    }

    /**
     * Handles the saving of the new album with the provided photos and album name.
     */
    @FXML
    private void handleSaveAlbum() {
        String albumName = albumNameField.getText().trim();
        if (albumName.isEmpty()) {
            new Alert(AlertType.ERROR, "Album name cannot be empty").showAndWait();
            return;
        }
        User currentUser = Controller.getCurrentUser();
        for (Album a : currentUser.getAlbums()) {
            if (a.getCaption().equals(albumName)) {
                new Alert(AlertType.ERROR, "Album already exists").showAndWait();
                return;
            }
        }
        Album newAlbum = new Album(albumName);
        for (File photoFile : photos) {
            boolean pictureExists = false;
            Picture existingPicture = null;
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
                newAlbum.addPicture(existingPicture);
                existingPicture.addToAlbum(newAlbum);
            } else {
                Picture newPicture = new Picture(photoFile.getPath());
                newAlbum.addPicture(newPicture);
                newPicture.addToAlbum(newAlbum);
            }
        }
        if (newAlbum.getPictures().size() == 0) {
            new Alert(AlertType.ERROR, "You must add at least one photo to the album").showAndWait();
            return;
        } else {
            Photos.addAlbum(currentUser, newAlbum);
            Photos.serializeUsers();
            userController.refreshAlbumGrid();
            Stage currentStage = (Stage) albumNameField.getScene().getWindow();
            currentStage.close();
        }
    }
}