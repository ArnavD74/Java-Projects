package photos24;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a picture in the Photos24 application.
 */
public class Picture implements Serializable {

    /**
     * Manually setting the serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * String to hold the location of the Picture.
     */
    private String name;

    /**
     * An ArrayList of Album objects that this Picture exists in.
     */
    private ArrayList<Album> albums;

    /**
     * An ArrayList of Tag objects that this Picture has instances of.
     */
    private ArrayList<Tag> tags;

    /**
     * String to hold the user-defined caption of the Picture.
     */
    private String caption;

    /**
     * Constructs a Picture with a name and initializes its properties.
     * 
     * @param name The name of the picture.
     */
    public Picture(String name) {
        this.name = name;
        getDateLastModified();
        this.albums = new ArrayList<Album>();
        this.tags = new ArrayList<Tag>();
        this.caption = "";
    }

    /**
     * Constructs a Picture with a name and caption.
     * 
     * @param name    The name of the picture.
     * @param caption The caption for the picture.
     */
    public Picture(String name, String caption) {
        this.name = name;
        getDateLastModified();
        this.albums = new ArrayList<Album>();
        this.tags = new ArrayList<Tag>();
        this.caption = caption;
    }

    /**
     * Gets the date when the picture was last modified.
     * 
     * @return The date of the last modification, or null if the file doesn't exist.
     */
    public Calendar getDateLastModified() {
        File file = new File(this.name);
        if (file.exists()) {
            long lastModified = file.lastModified();
            Calendar dateLastModified = Calendar.getInstance();
            dateLastModified.setTimeInMillis(lastModified);
            dateLastModified.set(Calendar.MILLISECOND, 0);
            return dateLastModified;
        } else {
            return null;
        }
    }

    /**
     * Retrieves a specific tag from the picture.
     * 
     * @param tagName The name of the tag.
     * @return The requested tag or null if not found.
     */
    public Tag getTag(String tagName) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tagName)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Retrieves all tags of the picture.
     * 
     * @return A list of tags.
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the picture.
     * 
     * @param name      The name of the tag.
     * @param tagValues The values for the tag.
     * @param multiple  Indicates if multiple values are allowed for the tag.
     */
    public void addTag(String name, ArrayList<String> tagValues, boolean multiple) {
        Tag t = getTag(name);
        if (t != null) {
            for (String v : tagValues) {
                if (!t.hasValue(v)) {
                    t.addValue(name);
                }
            }
        } else {
            Tag newTag = new Tag(name, tagValues, multiple);
            tags.add(newTag);
        }
    }

    /**
     * Updates the values of an existing tag.
     * 
     * @param tag       The tag to be updated.
     * @param newValues The new values for the tag.
     */
    public void updateTag(Tag tag, ArrayList<String> newValues) {
        Tag existingTag = null;
        for (Tag t : tags) {
            if (t.equals(tag)) {
                existingTag = t;
                break;
            }
        }
        if (existingTag != null) {
            if (tag.isMultiple()) {
                existingTag.clearValues();
                for (String value : newValues) {
                    existingTag.addValue(value);
                }
            } else {
                if (!newValues.isEmpty()) {
                    existingTag.setValue(newValues.get(0));
                }
            }
        } else {
            Tag newTag = new Tag(tag.getName(), newValues, tag.isMultiple());
            tags.add(newTag);
        }
    }

    /**
     * Removes a tag from the picture.
     * 
     * @param tagToRemove The tag to be removed.
     */
    public void removeTag(Tag tagToRemove) {
        if (tagToRemove != null && tags.contains(tagToRemove)) {
            tags.remove(tagToRemove);
        }
    }

    /**
     * Checks if the picture has a specific tag.
     * 
     * @param tagToCheck The tag to check.
     * @return True if the tag is present, false otherwise.
     */
    public boolean hasTag(Tag tagToCheck) {
        if (tagToCheck == null) {
            return false;
        }
        for (Tag tag : tags) {
            if (tag.equals(tagToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the name of the picture.
     * 
     * @return The name of the picture.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Adds the picture to an album.
     * 
     * @param a The album to which the picture is added.
     */
    public void addToAlbum(Album a) {
        albums.add(a);
    }

    /**
     * Removes the picture from an album.
     * 
     * @param a The album from which the picture is removed.
     */
    public void removeFromAlbum(Album a) {
        albums.remove(a);
    }

    /**
     * Changes the caption of the picture.
     * 
     * @param caption The new caption for the picture.
     */
    public void changeCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Retrieves the caption of the picture.
     * 
     * @return The caption of the picture.
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Retrieves the values of a specific tag.
     * 
     * @param inputTag The tag whose values are to be retrieved.
     * @return A list of values for the tag.
     */
    public List<String> getTagValues(Tag inputTag) {
        for (Tag tag : this.tags) {
            if (tag.equals(inputTag)) {
                return tag.getValues();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Removes a specific value from a tag.
     * 
     * @param tag           The tag from which the value is to be removed.
     * @param valueToDelete The value to be removed.
     */
    public void removeTagValue(Tag tag, String valueToDelete) {
        for (Tag existingTag : tags) {
            if (existingTag.equals(tag)) {
                existingTag.removeValue(valueToDelete);
                break;
            }
        }
    }

    /**
     * Clears all values from a tag.
     * 
     * @param tag The tag whose values are to be cleared.
     */
    public void clearValues(Tag tag) {
        for (Tag existingTag : this.tags) {
            if (existingTag.equals(tag)) {
                existingTag.clearValues();
                break;
            }
        }
    }

    /**
     * Sets a value for a tag, replacing any existing values.
     * 
     * @param tag   The tag for which the value is set.
     * @param value The value to be set for the tag.
     */
    public void setValue(Tag tag, String value) {
        for (Tag existingTag : this.tags) {
            if (existingTag.equals(tag)) {
                existingTag.setValue(value);
                break;
            }
        }
    }
}