package com.example.android24;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Picture implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String name;
    private final ArrayList<Album> albums;
    private final ArrayList<Tag> tags;
    private String caption;

    public Picture(String name) {
        this.name = name;
        this.albums = new ArrayList<Album>();
        this.tags = new ArrayList<Tag>();
        this.caption = "";
    }

    public String getName() {
        return this.name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void changeCaption(String s) {
        this.caption = s;
    }

    public ArrayList<Album> getAlbums() {
        return this.albums;
    }

    public ArrayList<Tag> getTags() {
        return this.tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Picture picture = (Picture) obj;
        return Objects.equals(name, picture.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}