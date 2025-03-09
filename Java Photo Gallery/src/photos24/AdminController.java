package photos24;

import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.Serializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for managing user accounts in the Photos24 application.
 */
public class AdminController implements Serializable {
    /**
     * Field where the user can enter an username
     */
    @FXML
    private TextField usernameField;

    /**
     * Displays a list of users
     */
    @FXML
    private ListView<String> usersList;

    /**
     * Initializes the controller and updates the user list.
     */
    public void initialize() {
        updateUserList();
    }

    /**
     * Shows an error alert with specified title and content.
     *
     * @param title   Title of the alert dialog.
     * @param content Content of the alert message.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Updates the list of users displayed on the UI.
     */
    private void updateUserList() {
        List<String> usernames = Photos.users.stream()
                .map(User::getName)
                .collect(Collectors.toList());
        ObservableList<String> observableUserList = FXCollections.observableArrayList(usernames);
        usersList.setItems(observableUserList);
    }

    /**
     * Handles adding a new user based on the input from the username field.
     * Validates the username and updates the user list.
     */
    @FXML
    private void handleAddUser() {
        String username = usernameField.getText();
        if (username.equals("")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Blank User Name");
            alert.setContentText("You must input a proper username. Please try again.");
            alert.showAndWait();
        } else if (username.equals("admin")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid User Name");
            alert.setContentText("You can not create a user named 'admin'. Please try again.");
            alert.showAndWait();
        } else if (username.equals("stock")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid User Name");
            alert.setContentText("You can not create a user named 'stock'. Please try again.");
            alert.showAndWait();
        } else {
            User user = Photos.getUser(username);
            if (user != null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("User Already Exists");
                alert.setContentText("The username you entered already exists. Please try again.");
                alert.showAndWait();
            } else {
                User newUser = new User(username);
                Photos.addUser(newUser);
                updateUserList();
            }
            usernameField.clear();
        }
    }

    /**
     * Handles removing a user based on the input from the username field.
     * Validates the username and updates the user list.
     */
    @FXML
    private void handleRemoveUser() {
        String username = usernameField.getText();
        User user = Photos.getUser(username);
        if (user == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("User Does Not Exist");
            alert.setContentText("The username you entered does not exist. Please try again.");
            alert.showAndWait();
        } else {
            Photos.removeUser(user);
            updateUserList();
        }
        usernameField.clear();
    }

    /**
     * Handles the logout process and loads the login interface.
     *
     * @param event The event that triggered this method.
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
}
