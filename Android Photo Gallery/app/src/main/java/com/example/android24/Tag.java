package com.example.android24;

import java.util.ArrayList;
import java.io.Serializable;

public class Tag implements Serializable {
    private final String name;
    private final ArrayList<String> values;
    private final boolean isMultiple;
    private final String tagType;
    private static final long serialVersionUID = 2L;

    public Tag(String name, boolean multiple, String tagType) {
        this.name = name;
        values = new ArrayList<String>();
        isMultiple = multiple;
        this.tagType = tagType;
    }


    public String getName() {
        return this.name;
    }

    public boolean isMultiple() {
        return this.isMultiple;
    }

    public ArrayList<String> getValues() {
        return this.values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public void removeValue(String value) {
        this.values.remove(value);
    }

    public String getTagType() {
        return this.tagType;
    }
}