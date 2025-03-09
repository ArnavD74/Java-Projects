package photos24;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;

/**
 * Main application class for Photos24.
 * Manages users, albums, and serialization of data.
 */
public class Photos extends Application implements Serializable {

    /**
     * Manually setting the serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ArrayList of currently existing User objects.
     */
    static ArrayList<User> users = new ArrayList<>();

    /**
     * Retrieves albums for a given user.
     * 
     * @param u The user whose albums are to be retrieved.
     * @return ArrayList of albums.
     */
    public static ArrayList<Album> getAlbums(User u) {
        return u.getAlbums();
    }

    /**
     * Adds an album to a user.
     * 
     * @param u     The user to whom the album will be added.
     * @param album The album to add.
     */
    public static void addAlbum(User u, Album album) {
        u.addAlbum(album);
    }

    /**
     * Adds a user to the application.
     * 
     * @param u The user to add.
     */
    public static void addUser(User u) {
        Photos.users.add(u);
        serializeUsers();
    }

    /**
     * Removes a user from the application.
     * 
     * @param u The user to remove.
     */
    public static void removeUser(User u) {
        Photos.users.remove(u);
        serializeUsers();
    }

    /**
     * Retrieves all users.
     * 
     * @return ArrayList of all users.
     */
    public static ArrayList<User> getAllUsers() {
        return Photos.users;
    }

    /**
     * Retrieves a user by their username.
     * 
     * @param username The username of the user.
     * @return The user if found, null otherwise.
     */
    public static User getUser(String username) {
        for (User user : Photos.users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Serializes users to disk.
     */
    public static void serializeUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            out.writeObject(Photos.users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes users from disk.
     */
    @SuppressWarnings("unchecked")
    public static void deserializeUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("users.dat"))) {
            Object obj = in.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> tempList = (ArrayList<?>) obj;
                if (!tempList.isEmpty() && tempList.get(0) instanceof User) {
                    Photos.users = (ArrayList<User>) tempList;
                } else {
                }
            } else {
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Screen");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the application state to disk.
     * 
     * @param app The Photos application to write.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeApp(Photos app)
            throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("dat" + File.separator + "points.dat"))) {
            oos.writeObject(app);
        }
    }

    /**
     * The main method of the Application that launches the loginController.
     * 
     * @param args The launch arguments when running the application.
     */
    public static void main(String[] args) {
        deserializeUsers();
        launch(args);
    }
}