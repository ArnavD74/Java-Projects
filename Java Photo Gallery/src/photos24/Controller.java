package photos24;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Main controller for the Photos24 application.
 */
public class Controller implements Serializable {

    /**
     * Mannually set the serial UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Current user
     */
    public static User currentUser;

    /**
     * Takes a username from the user
     */
    @FXML
    private TextField usernameField;

    /**
     * Takes a password from the user
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Initializes the controller, including creating a stock user with default
     * pictures if not already present.
     */
    @FXML
    public void initialize() {
        boolean stockExists = false;
        ArrayList<User> allUsers = Photos.getAllUsers();
        for (User u : allUsers) {
            if (u.getName().equals("stock")) {
                stockExists = true;
            }
        }
        if (!stockExists) {
            User stock = new User("stock");
            Photos.addUser(stock);
            Album stockAlbum = new Album("stock");
            Picture[] defaultPictures = {
                    new Picture("data/ball.jpeg", "curled up"),
                    new Picture("data/curious.jpeg", "looking over a window"),
                    new Picture("data/hello.jpeg", "please pet me!"),
                    new Picture("data/lazy.jpeg", "laying in the warmth"),
                    new Picture("data/sun.jpeg", "enjoying the sunshine"),
                    new Picture("data/watching.jpeg", "reading the CS213 lecture slides")
            };
            for (Picture picture : defaultPictures) {
                stockAlbum.addPicture(picture);
                picture.addToAlbum(stockAlbum);
            }
            Photos.addAlbum(stock, stockAlbum);
            Photos.serializeUsers();
        }
    }

    /**
     * Handles the login button click event.
     * Authenticates the user and opens the corresponding admin or user panel.
     * 
     * @param event The action event triggered by clicking the login button.
     */
    @FXML
    private void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        currentUser = Photos.getUser(username);
        User user = Photos.getUser(username);
        if (username.equals("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Admin Panel");
                stage.setScene(new Scene(root));
                stage.show();
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (user == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("User Not Found");
            alert.setContentText("The username you entered does not exist. Please try again.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("user.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("User Panel");
                stage.setScene(new Scene(root));
                stage.show();
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the current user of the application.
     * 
     * @return The current user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }
}