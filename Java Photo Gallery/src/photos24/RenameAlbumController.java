package photos24;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller for renaming albums in the Photos24 application.
 */
public class RenameAlbumController {

    /**
     * A TextField object to hold and edit the name of the album to be edited.
     */
    @FXML
    private TextField currentAlbumNameField;

    /**
     * A TextField object to hold and edit the name of the album's new name.
     */
    @FXML
    private TextField newAlbumNameField;

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
     * Handles the renaming of an album.
     */
    @FXML
    private void handleRenameAlbum() {
        String currentAlbumName = currentAlbumNameField.getText().trim();
        String newAlbumName = newAlbumNameField.getText().trim();
        User currentUser = Controller.getCurrentUser();
        for (Album a : currentUser.getAlbums()) {
            if (a.getCaption().equals(newAlbumName)) {
                new Alert(AlertType.ERROR, "New album name already exists").showAndWait();
                return;
            }
        }
        Album album = null;
        for (Album a : currentUser.getAlbums()) {
            if (a.getCaption().equals(currentAlbumName)) {
                album = a;
                break;
            }
        }
        if (album == null) {
            new Alert(AlertType.ERROR, "Could not find the source album").showAndWait();
            return;
        } else {
            album.changeCaption(newAlbumName);
        }
        Photos.serializeUsers();
        userController.refreshAlbumGrid();
    }
}
