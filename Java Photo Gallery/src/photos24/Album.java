package photos24;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Represents an album in the Photos24 application.
 * An album contains a collection of pictures and a caption.
 */
public class Album implements Serializable {

    /**
     * Manually setting the serail UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of Picture objects
     */
    private ArrayList<Picture> pictures;

    /**
     * String that stores the caption of the album
     */
    private String caption;

    /**
     * Constructs a new Album with the given caption.
     *
     * @param caption The caption for the album.
     */
    public Album(String caption) {
        this.caption = caption;
        this.pictures = new ArrayList<Picture>();
    }

    /**
     * Adds a picture to the album.
     *
     * @param p The Picture to be added.
     */
    public void addPicture(Picture p) {
        this.pictures.add(p);
    }

    /**
     * Deletes a picture from the album.
     *
     * @param p The Picture to be deleted.
     * @return true if the album still has pictures after the deletion, false
     *         otherwise.
     */
    public boolean deletePicture(Picture p) {
        this.pictures.remove(p);
        if (this.pictures.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Changes the caption of the album.
     *
     * @param newCaption The new caption for the album.
     */
    public void changeCaption(String newCaption) {
        this.caption = newCaption;
    }

    /**
     * Gets the caption of the album.
     *
     * @return The caption of the album.
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Retrieves all pictures in the album.
     *
     * @return A list of pictures in the album.
     */
    public ArrayList<Picture> getPictures() {
        return this.pictures;
    }

    /**
     * Gets the range of dates when pictures in the album were last modified.
     *
     * @return A string representing the date range in "MM/dd/yyyy" format,
     *         or a message indicating if no dates are available or valid.
     */
    public String getDateRanges() {
        if (pictures == null || pictures.isEmpty()) {
            return "No dates available";
        }
        Calendar minDate = null;
        Calendar maxDate = null;
        for (Picture picture : pictures) {
            Calendar date = picture.getDateLastModified();
            if (date != null) {
                if (minDate == null || date.before(minDate)) {
                    minDate = date;
                }
                if (maxDate == null || date.after(maxDate)) {
                    maxDate = date;
                }
            }
        }
        if (minDate == null || maxDate == null) {
            return "No valid dates found";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(minDate.getTime()) + " - " + sdf.format(maxDate.getTime());
    }
}
