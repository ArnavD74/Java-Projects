package photos24;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

/**
 * Controller for handling album operations in the Photos24 application.
 */
public class AlbumController {
    /**
     * Grid that displays all the photos
     */
    @FXML
    private TilePane photoGrid;

    /**
     * List of Pictures
     */
    @FXML
    private ListView<Picture> photoListView;

    /**
     * Displays photo
     */
    @FXML
    private ImageView photoDisplayArea;

    /**
     * Buttons that handles the actions: add photo, remove photo, move photo and
     * copy photo
     */
    @FXML
    private Button addPhotoButton, removePhotoButton, movePhotoButton, copyPhotoButton;

    /**
     * Button that goes to search panel
     */
    @FXML
    private Button searchButton;

    /**
     * Button that goes to the slideshow that displays all photos
     */
    @FXML
    private Button slideshowButton;

    /**
     * Current album
     */
    private Album currentAlbum;

    /**
     * Photo that is selected
     */
    private Picture selectedPicture = null;

    /**
     * Contains the selected photo
     */
    private StackPane selectedImageContainer = null;

    /**
     * Sets the current album and displays its photos.
     * 
     * @param album The album to be set as the current album.
     */
    public void setCurrentAlbum(Album album) {
        this.currentAlbum = album;
        displayPhotos();
    }

    /**
     * Displays photos of the current album in the photo grid.
     */
    private void displayPhotos() {
        photoGrid.getChildren().clear();
        photoGrid.setPadding(new Insets(10, 10, 30, 10));
        photoGrid.setHgap(15);
        photoGrid.setVgap(15);

        for (Picture picture : currentAlbum.getPictures()) {
            ImageView imageView = new ImageView(new Image("file:" + picture.getName()));
            imageView.setFitHeight(120);
            imageView.setFitWidth(120);
            imageView.setPreserveRatio(true);

            String captionText = picture.getCaption() != null ? picture.getCaption() : "";
            if (captionText.length() > 18) {
                captionText = captionText.substring(0, 15) + "...";
            }

            Label captionLabel = new Label(captionText);
            captionLabel.setAlignment(Pos.CENTER);
            captionLabel.setMaxWidth(120);

            StackPane imageContainer = new StackPane(imageView);
            imageContainer.setBorder(new Border(
                    new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                            new BorderWidths(2))));
            imageContainer.setOnMouseClicked(event -> {
                if (selectedImageContainer != null) {
                    selectedImageContainer.setBorder(new Border(
                            new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                                    new BorderWidths(2))));
                }
                selectedImageContainer = imageContainer;
                selectedImageContainer.setBorder(new Border(
                        new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
                selectedPicture = picture;
            });

            VBox vbox = new VBox(imageContainer, captionLabel);
            vbox.setAlignment(Pos.CENTER);

            photoGrid.getChildren().add(vbox);
        }
    }

    /**
     * Refreshes the album by redisplaying the photos.
     */
    private void refreshAlbum() {
        displayPhotos();
    }

    /**
     * Handles the addition of a photo to the album.
     */
    @FXML
    private void addPhoto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addphoto.fxml"));
            Parent root = loader.load();
            AddPhotoController addPhotoController = loader.getController();
            addPhotoController.setCurrentAlbum(currentAlbum);
            Stage stage = new Stage();
            stage.setTitle("Add Photo to Album");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshAlbum();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshAlbum();
    }

    /**
     * Handles the removal of a photo from the album.
     */
    @FXML
    private void removePhoto() {
        if (selectedPicture != null) {
            currentAlbum.getPictures().remove(selectedPicture);
            selectedPicture.removeFromAlbum(currentAlbum);
            displayPhotos();
            selectedImageContainer = null;
            selectedPicture = null;
            Photos.serializeUsers();
        } else {
            refreshAlbum();
        }
        refreshAlbum();
    }

    /**
     * Handles the preview of a selected photo.
     */
    @FXML
    private void handlePreviewPhoto() {
        displaySelectedPhoto(selectedPicture);
    }

    /**
     * Displays the selected photo in a preview stage.
     * 
     * @param p The picture to display.
     */
    private void displaySelectedPhoto(Picture p) {
        if (p == null) {
            return;
        }
        Stage previewStage = new Stage();
        previewStage.initModality(Modality.APPLICATION_MODAL);
        ImageView previewImageView = new ImageView(new Image("file:" + p.getName()));
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitHeight(600);
        previewImageView.setFitWidth(800);
        String captionText = p.getCaption();
        if (captionText == null || captionText.isEmpty()) {
            captionText = "No caption";
        }
        Label captionLabel = new Label(captionText);
        captionLabel.setWrapText(true);
        if (p.getCaption() == null || p.getCaption().isEmpty()) {
            captionLabel.setStyle("-fx-text-fill: grey;");
        }
        Button captionButton = new Button("Add/Change Caption");
        captionButton.setOnAction(event -> handleAddChangeCaption(p, captionLabel));
        Button tagButton = new Button("Add/Change Tags");
        tagButton.setOnAction(event -> handleAddChangeTags(p));
        ArrayList<Tag> tags = (ArrayList<Tag>) p.getTags();
        StringBuilder tagLine = new StringBuilder();
        for (Tag tag : tags) {
            tagLine.append(tag.getName()).append(": ");
            ArrayList<String> values = tag.getValues();
            tagLine.append(String.join(", ", values));
            tagLine.append(" • ");
        }
        if (tagLine.length() > 0) {
            tagLine.setLength(tagLine.length() - 3);
        } else {
            tagLine.append("No tags");
        }
        Label tagLabel = new Label(tagLine.toString());
        tagLabel.setWrapText(true);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String dateLastModified = sdf.format(p.getDateLastModified().getTime());
        Label dateLabel = new Label("Last Modified: " + dateLastModified);
        VBox layout = new VBox(10, previewImageView, captionLabel, tagLabel, dateLabel, captionButton, tagButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        previewStage.setScene(scene);
        previewStage.setTitle(p.getName());
        previewStage.show();
        refreshAlbum();
    }

    /**
     * Handles adding or changing the caption of a picture.
     * 
     * @param picture      The picture to modify.
     * @param captionLabel The label displaying the picture's caption.
     */
    private void handleAddChangeCaption(Picture picture, Label captionLabel) {
        Stage captionStage = new Stage();
        captionStage.initModality(Modality.APPLICATION_MODAL);
        TextField captionField = new TextField();
        captionField.setPromptText("Enter caption");
        captionField.setText(picture.getCaption());
        Button enterButton = new Button("Enter");
        enterButton.setOnAction(e -> {
            String newCaption = captionField.getText();
            picture.changeCaption(newCaption);
            captionLabel.setText(newCaption);
            captionStage.close();
        });
        VBox layout = new VBox(10, captionField, enterButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        captionStage.setScene(scene);
        captionStage.setTitle("Add/Change Caption");
        captionStage.show();
        Photos.serializeUsers();
    }

    /**
     * Handles adding or changing the tags of a picture.
     * 
     * @param picture The picture to modify.
     */
    private void handleAddChangeTags(Picture picture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tag.fxml"));
            Parent root = loader.load();
            TagController tagController = loader.getController();
            tagController.setCurrentUserAndPicture(Controller.getCurrentUser(), picture);
            Stage tagStage = new Stage();
            tagStage.initModality(Modality.APPLICATION_MODAL);
            tagStage.setTitle("Add/Change Tags");
            tagStage.setScene(new Scene(root));
            tagStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves the selected photo to another album.
     * 
     * @param event The event that triggered the method.
     */
    public void movePhoto(ActionEvent event) {
        if (selectedPicture == null) {
            showAlert("No picture selected", "Please select a picture to move.");
            return;
        }
        String targetAlbumCaption = askForAlbumCaption();
        if (targetAlbumCaption == null || targetAlbumCaption.isEmpty()) {
            return;
        }
        Album targetAlbum = findAlbumByCaption(targetAlbumCaption);
        if (targetAlbum != null && !targetAlbum.equals(currentAlbum)) {
            if (isPictureInAlbum(targetAlbum, selectedPicture)) {
                showAlert("Error", "Target album already contains a photo with the same name.");
                return;
            }
            currentAlbum.getPictures().remove(selectedPicture);
            targetAlbum.getPictures().add(selectedPicture);
            showAlert("Success", "Picture moved to '" + targetAlbumCaption + "'.");
            Photos.serializeUsers();
            refreshAlbum();
        } else {
            showAlert("Error", "Album '" + targetAlbumCaption + "' does not exist or is the current album.");
        }
    }

    /**
     * Checks if a picture is in a specified album.
     * 
     * @param album   The album to check.
     * @param picture The picture to look for.
     * @return True if the picture is in the album, false otherwise.
     */
    private boolean isPictureInAlbum(Album album, Picture picture) {
        for (Picture p : album.getPictures()) {
            if (p.getName().equalsIgnoreCase(picture.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copies the selected photo to another album.
     * 
     * @param event The event that triggered the method.
     */
    public void copyPhoto(ActionEvent event) {
        if (selectedPicture == null) {
            showAlert("No Picture selected", "Please select a picture to copy.");
            return;
        }
        String targetAlbumCaption = askForAlbumCaption();
        if (targetAlbumCaption == null || targetAlbumCaption.isEmpty()) {
            return;
        }
        Album targetAlbum = findAlbumByCaption(targetAlbumCaption);
        if (targetAlbum != null && !targetAlbum.equals(currentAlbum)) {
            if (isPictureInAlbum(targetAlbum, selectedPicture)) {
                showAlert("Error", "Target album already contains a photo with the same name.");
                return;
            }
            Picture copiedPicture = selectedPicture;
            targetAlbum.getPictures().add(copiedPicture);
            showAlert("Success", "Picture copied to '" + targetAlbumCaption + "'.");
            Photos.serializeUsers();
            refreshAlbum();
        } else {
            showAlert("Error", "Album '" + targetAlbumCaption + "' does not exist or is the current album.");
        }
    }

    /**
     * Asks for an album caption via a dialog.
     * 
     * @return The entered album caption or null if none entered.
     */
    private String askForAlbumCaption() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Picture Movement");
        dialog.setHeaderText("Enter the name of the target album:");
        Optional<String> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }

    /**
     * Finds an album by its caption.
     * 
     * @param albumCaption The caption of the album.
     * @return The found album or null if not found.
     */
    private Album findAlbumByCaption(String albumCaption) {
        User currentUser = Controller.getCurrentUser();
        for (Album album : currentUser.getAlbums()) {
            if (album.getCaption().equalsIgnoreCase(albumCaption)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Shows an alert with specified title and content.
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
     * Handles starting a slideshow of pictures in the album.
     */
    @FXML
    private void handleSlideshow() {
        if (currentAlbum.getPictures().isEmpty()) {
            showAlert("No Photos", "The album is empty.");
            return;
        }

        Stage slideshowStage = new Stage();
        slideshowStage.initModality(Modality.APPLICATION_MODAL);

        ImageView slideshowImageView = new ImageView();
        slideshowImageView.setPreserveRatio(true);
        slideshowImageView.setFitHeight(600);
        slideshowImageView.setFitWidth(800);

        int[] currentIndex = { 0 };
        setImageForSlideshow(slideshowImageView, currentAlbum.getPictures().get(currentIndex[0]));

        slideshowStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                currentIndex[0] = (currentIndex[0] + 1) % currentAlbum.getPictures().size();
                setImageForSlideshow(slideshowImageView, currentAlbum.getPictures().get(currentIndex[0]));
            } else if (event.getCode() == KeyCode.LEFT) {
                currentIndex[0] = (currentIndex[0] - 1 + currentAlbum.getPictures().size())
                        % currentAlbum.getPictures().size();
                setImageForSlideshow(slideshowImageView, currentAlbum.getPictures().get(currentIndex[0]));
            }
        });

        Label navigationInstructions = new Label("Use the arrow keys to navigate between pictures.");
        navigationInstructions.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

        VBox layout = new VBox(10, slideshowImageView, navigationInstructions);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        slideshowStage.setScene(scene);
        slideshowStage.setTitle("Slideshow");
        slideshowStage.show();
    }

    /**
     * Sets the image for the slideshow.
     * 
     * @param imageView The ImageView for the slideshow.
     * @param picture   The picture to display.
     */
    private void setImageForSlideshow(ImageView imageView, Picture picture) {
        imageView.setImage(new Image("file:" + picture.getName()));
    }

}
