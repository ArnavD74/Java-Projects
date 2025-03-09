package photos24;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

/**
 * Controller for user interactions in the Photos24 application.
 */
public class UserController {

    /**
     * The TilePane object that holds the album grid.
     */
    @FXML
    private TilePane albumsGrid;

    /**
     * The User object to represent the current user of the Controller.
     */
    public User currentUser = Controller.getCurrentUser();

    /**
     * The label for the greeting seen in the top right of the Controller window.
     */
    @FXML
    public Label greetingLabel;

    /**
     * Initializes the UserController, loads albums, and sets the greeting label.
     */
    @FXML
    public void initialize() {
        loadAlbums();
        greetingLabel.setText("Hello, " + currentUser.getName() + "! You have " + currentUser.getAlbums().size()
                + " albums consisting of " + currentUser.getPictures().size() + " total pictures.");
        resizeStage();
        Node someNode = greetingLabel;
        someNode.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (isNowFocused) {
                                refreshAlbumGrid();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Refreshes the album grid view.
     */
    public void refreshAlbumGrid() {
        currentUser.getAlbums().removeIf(album -> album.getPictures().isEmpty());
        greetingLabel.setText("Hello, " + currentUser.getName() + "! You have " + currentUser.getAlbums().size()
                + " albums consisting of " + currentUser.getPictures().size() + " total pictures.");
        resizeStage();
        loadAlbums();
    }

    /**
     * Shows an alert using FXML Error box.
     * 
     * @param title   the title of the error to use as the window title.
     * @param content the content of the error to display.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Resizes the stage to fit all of the currently existing albums.
     */
    private void resizeStage() {
        Platform.runLater(() -> {
            Scene scene = albumsGrid.getScene();
            if (scene == null) {
                return;
            }
            Stage stage = (Stage) scene.getWindow();
            if (stage == null) {
                return;
            }
            albumsGrid.applyCss();
            albumsGrid.layout();
            double width = albumsGrid.prefWidth(-1);
            int columnCount = 4;
            int rowCount = (int) Math.ceil((double) albumsGrid.getChildren().size() / columnCount);
            double height = albumsGrid.prefHeight(-1);
            height += rowCount * 200;
            stage.setWidth(width + 40);
            stage.setHeight(height + 60);
        });
    }

    /**
     * Loads albums into the grid view.
     */
    private void loadAlbums() {
        ArrayList<Album> userAlbums = Photos.getAlbums(currentUser);
        albumsGrid.getChildren().clear();
        for (Album album : userAlbums) {
            VBox albumBox = new VBox();
            albumBox.setSpacing(5);
            ImageView thumbnail = createAlbumThumbnail(album);
            Button albumTile = new Button();
            albumTile.setGraphic(thumbnail);
            albumTile.setOnAction(event -> openAlbum(album));
            albumBox.getChildren().add(albumTile);
            Label caption = new Label(album.getCaption());
            caption.setStyle("-fx-font-weight: bold;");
            albumBox.getChildren().add(caption);
            Label photoCount = new Label(album.getPictures().size() + " photos");
            albumBox.getChildren().add(photoCount);
            String dateRange = album.getDateRanges();
            Label dateRangeLabel = new Label(dateRange);
            albumBox.getChildren().add(dateRangeLabel);
            albumsGrid.getChildren().add(albumBox);
        }
        resizeStage();
    }

    /**
     * Creates a thumbnail for an album.
     * 
     * @param album The album for which to create a thumbnail.
     * @return An ImageView containing the thumbnail.
     */
    private ImageView createAlbumThumbnail(Album album) {
        ImageView thumbnail = new ImageView();
        if (!album.getPictures().isEmpty()) {
            Picture firstPicture = album.getPictures().get(0);
            Image image = new Image(new File(firstPicture.getName()).toURI().toString());
            thumbnail.setImage(image);
        }
        thumbnail.setFitWidth(120);
        thumbnail.setFitHeight(120);
        resizeStage();
        return thumbnail;
    }

    /**
     * Handles the creation of a new album.
     */
    @FXML
    private void handleCreateNewAlbum() {
        resizeStage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createAlbum.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create New Album");
            stage.setScene(new Scene(root));
            CreateAlbumController controller = loader.getController();
            controller.setUserController(this);
            stage.show();
            resizeStage();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Load Error");
            alert.setContentText("Could not load the Create Album interface.");
            alert.showAndWait();
        }
        resizeStage();
    }

    /**
     * Handles the deletion of a new album.
     */
    @FXML
    private void handleDeleteAlbum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deletealbum.fxml"));
            Parent root = loader.load();
            DeleteAlbumController controller = loader.getController();
            controller.setUserController(this);
            Stage stage = new Stage();
            stage.setTitle("Delete Album");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Delete Album interface.");
        }
    }

    /**
     * Handles the renaming of a new album.
     */
    @FXML
    private void handleRenameAlbum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("renamealbum.fxml"));
            Parent root = loader.load();
            RenameAlbumController controller = loader.getController();
            controller.setUserController(this);
            Stage stage = new Stage();
            stage.setTitle("Rename Album");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the Rename Album interface.");
        }
    }

    /**
     * Opens an album.
     * 
     * @param album The album to open.
     */
    private void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("albumcontroller.fxml"));
            Parent root = loader.load();
            AlbumController albumController = loader.getController();
            albumController.setCurrentAlbum(album);
            Stage stage = new Stage();
            stage.setTitle("Album: " + album.getCaption());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles user logout.
     * 
     * @param event The event that triggered the method.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        Photos.serializeUsers();
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            stage.setWidth(300);
            stage.setHeight(180);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the login interface.");
        }
    }

    /**
     * Handles the search for photos.
     */
    @FXML
    private void handleSearchPhotos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Search Photos");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}