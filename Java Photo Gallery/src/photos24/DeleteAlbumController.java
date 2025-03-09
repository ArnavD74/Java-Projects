package photos24;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller for handling the deletion of albums in the Photos24 application.
 */
public class DeleteAlbumController {

    /**
     * TextField to hold the name of the Album to be deleted.
     */
    @FXML
    private TextField albumNameField;

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
     * Handles the deletion of an album based on the album name entered.
     */
    @FXML
    private void handleDeleteAlbum() {
        String albumName = albumNameField.getText().trim();
        User currentUser = Controller.getCurrentUser();
        Album album = null;
        for (Album a : currentUser.getAlbums()) {
            if (a.getCaption().equals(albumName)) {
                album = a;
                break;
            }
        }
        if (album != null) {
            currentUser.deleteAlbum(album);
        } else {
            new Alert(AlertType.ERROR, "Album does not exist").showAndWait();
            return;
        }
        Photos.serializeUsers();
        userController.refreshAlbumGrid();
    }
}
