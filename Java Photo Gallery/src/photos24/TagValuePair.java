package photos24;

/**
 * Represents a tag-value pair, potentially associated with search operators.
 */
public class TagValuePair {

    /**
     * Enum to hold the value of the potential search operator.
     */
    public enum Operator {
        /**
         * Logical 'AND' operation.
         */
        AND,

        /**
         * Logical 'OR' operation.
         */
        OR
    }

    /**
     * String to hold the name of the tag.
     */
    private final String tagName;

    /**
     * String to hold the value of the tag.
     */
    private final String tagValue;

    /**
     * The operator enum that applies to the tag.
     */
    private Operator operator;

    /**
     * Constructs a TagValuePair with a tag name and value.
     * 
     * @param tagName  The name of the tag.
     * @param tagValue The value of the tag.
     */
    public TagValuePair(String tagName, String tagValue) {
        this(tagName, tagValue, null);
    }

    /**
     * Constructs a TagValuePair with a tag name, value, and operator.
     * 
     * @param tagName  The name of the tag.
     * @param tagValue The value of the tag.
     * @param operator The operator associated with the tag-value pair.
     */
    public TagValuePair(String tagName, String tagValue, Operator operator) {
        this.tagName = tagName;
        this.tagValue = tagValue;
        this.operator = operator;
    }

    /**
     * Retrieves the tag name.
     * 
     * @return The name of the tag.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Retrieves the tag value.
     * 
     * @return The value of the tag.
     */
    public String getTagValue() {
        return tagValue;
    }

    /**
     * Retrieves the operator.
     * 
     * @return The operator.
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Overridden toString method to return the tag name, value, and possible
     * operator
     * 
     */
    @Override
    public String toString() {
        if (operator == null) {
            return tagName + "=" + tagValue;
        } else {
            return tagName + "=" + tagValue + " " + operator.toString();
        }
    }
}