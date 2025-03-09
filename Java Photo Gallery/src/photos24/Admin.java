package photos24;

import java.io.*;
import java.util.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Represents an administrator in the Photos24 application with capabilities
 * to manage user accounts.
 */
public class Admin implements Serializable {

    /**
     * Manually setting the serail UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Lists all users in the Photos24 application.
     *
     * @return A string containing the names of all users, each name on a new line.
     */
    public String listUsers() {
        ArrayList<User> allUser = Photos.getAllUsers();
        String users = "";
        for (User u : allUser)
            users += u.getName() + "\n";
        return users;
    }

    /**
     * Adds a user to the Photos24 application.
     * Displays an error alert if the username does not exist.
     *
     * @param username The username of the user to be added.
     */
    public void addUser(String username) {
        User u = Photos.getUser(username);
        if (u != null) {
            Photos.addUser(u);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("User Not Found");
            alert.setContentText("The username you entered does not exist. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Deletes a user from the Photos24 application.
     * Displays an error alert if the username does not exist.
     *
     * @param username The username of the user to be deleted.
     */
    public void deleteUser(String username) {
        User u = Photos.getUser(username);
        if (u != null) {
            Photos.removeUser(u);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("User Not Found");
            alert.setContentText("The username you entered does not exist. Please try again.");
            alert.showAndWait();
        }
    }
}