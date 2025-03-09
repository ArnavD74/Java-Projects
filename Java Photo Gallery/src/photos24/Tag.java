package photos24;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a tag in the Photos24 application.
 */
public class Tag implements Serializable {

    /**
     * Manually setting the serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * String to hold the Tag's name.
     */
    private String name;

    /**
     * An ArrayList of String objects to hold the value of the tag, if multiple.
     */
    private ArrayList<String> values;

    /**
     * A boolean to discern if a tag can hold multiple values or not.
     */
    private boolean multiple;

    /**
     * Constructs a Tag with specified name, values, and multiple flag.
     * 
     * @param name     The name of the tag.
     * @param values   The values of the tag.
     * @param multiple Flag indicating if multiple values are allowed.
     */
    public Tag(String name, ArrayList<String> values, boolean multiple) {
        this.name = name;
        this.values = values;
        this.multiple = multiple;
    }

    /**
     * Adds a value to the tag.
     * 
     * @param value The value to add.
     * @throws IllegalStateException If the tag doesn't allow multiple values or if
     *                               the value already exists.
     */
    public void addValue(String value) {
        if (!multiple && !values.isEmpty()) {
            throw new IllegalStateException("This tag does not allow multiple values");
        }
        if (!values.contains(value)) {
            values.add(value);
        } else {
            throw new IllegalStateException("Value already exists");
        }
    }

    /**
     * Removes a value from the tag.
     * 
     * @param value The value to remove.
     * @throws IllegalArgumentException If the value is null.
     * @throws IllegalStateException    If the value does not exist in this tag.
     */
    public void removeValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        boolean removed = values.remove(value);
        if (!removed) {
            throw new IllegalStateException("Value '" + value + "' does not exist in this tag");
        }
    }

    /**
     * Sets the name of the tag.
     * 
     * @param name The new name of the tag.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the tag.
     * 
     * @return The name of the tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the tag has a specific value.
     * 
     * @param value The value to check.
     * @return True if the value exists, false otherwise.
     */
    public boolean hasValue(String value) {
        for (String v : values) {
            if (value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the tag allows multiple values.
     * 
     * @return True if multiple values are allowed, false otherwise.
     */
    public boolean isMultiple() {
        return this.multiple;
    }

    /**
     * Retrieves the values of the tag.
     * 
     * @return An ArrayList of the values.
     */
    public ArrayList<String> getValues() {
        return this.values;
    }

    /**
     * Clears all values from the tag.
     */
    public void clearValues() {
        this.values.clear();
    }

    /**
     * Sets a single value for the tag, replacing any existing values.
     * 
     * @param value The new value for the tag.
     */
    public void setValue(String value) {
        this.values.clear();
        this.values.add(value);
    }

    /**
     * Sets multiple values for the tag, replacing any existing values.
     * 
     * @param newValues The new values for the tag.
     */
    public void setValues(ArrayList<String> newValues) {
        this.values.clear();
        this.values.addAll(newValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.getName() + ": " + String.join(", ", this.getValues());
    }
}