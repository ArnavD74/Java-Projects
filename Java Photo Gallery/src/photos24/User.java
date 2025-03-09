package photos24;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a user in the Photos24 application.
 */
public class User implements Serializable {

  /**
   * Manually setting the serial UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * String to hold the User's name.
   */
  private String username;

  /**
   * ArrayList of album objects unique to this User.
   */
  private ArrayList<Album> albums;

  /**
   * ArrayList of Tag objects unique to this User.
   */
  private ArrayList<Tag> tags;

  /**
   * Constructs a User with a given username.
   * Initializes albums and predefined tags.
   * 
   * @param username The username of the user.
   */
  public User(String username) {
    this.username = username;
    this.albums = new ArrayList<Album>();
    this.tags = new ArrayList<Tag>();
    ArrayList<String> default1vals = new ArrayList<String>();
    Tag default1 = new Tag("People", default1vals, true);
    ArrayList<String> default2vals = new ArrayList<String>();
    Tag default2 = new Tag("Location", default2vals, false);
    ArrayList<String> default3vals = new ArrayList<String>();
    Tag default3 = new Tag("Colors", default3vals, true);
    tags.add(default1);
    tags.add(default2);
    tags.add(default3);
  }

  /**
   * Retrieves the name of the user.
   * 
   * @return The username.
   */
  public String getName() {
    return this.username;
  }

  /**
   * Retrieves the albums of the user.
   * 
   * @return A list of albums.
   */
  public ArrayList<Album> getAlbums() {
    return this.albums;
  }

  /**
   * Retrieves the tags associated with the user.
   * 
   * @return A list of tags.
   */
  public ArrayList<Tag> getTags() {
    return this.tags;
  }

  /**
   * Adds an album to the user's collection.
   * 
   * @param album The album to be added.
   */
  public void addAlbum(Album album) {
    albums.add(album);
  }

  /**
   * Deletes an album from the user's collection.
   * 
   * @param album The album to be deleted.
   */
  public void deleteAlbum(Album album) {
    albums.remove(album);
  }

  /**
   * Adds a tag to the user's collection.
   * 
   * @param tag The tag to be added.
   */
  public void addTag(Tag tag) {
    this.tags.add(tag);
  }

  /**
   * Retrieves user-specific tags.
   * 
   * @return A list of user-specific tags.
   */
  public ArrayList<Tag> getUserTags() {
    return this.tags;
  }

  /**
   * Removes a user-specific tag.
   * 
   * @param tag The tag to be removed.
   */
  public void removeUserTag(Tag tag) {
    this.tags.remove(tag);
  }

  /**
   * Retrieves all pictures from the user's albums.
   * 
   * @return A list of pictures.
   */
  public ArrayList<Picture> getPictures() {
    ArrayList<Picture> allPics = new ArrayList<Picture>();
    for (Album a : albums) {
      for (Picture p : a.getPictures()) {
        allPics.add(p);
      }
    }
    return allPics;
  }

  /**
   * Updates a tag in the user's collection.
   * 
   * @param tagToUpdate The tag to be updated.
   */
  public void updateTag(Tag tagToUpdate) {
    for (Tag tag : tags) {
      if (tag.equals(tagToUpdate)) {
        tag.setValues(tagToUpdate.getValues());
        break;
      }
    }
  }
}