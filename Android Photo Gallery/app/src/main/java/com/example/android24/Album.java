package com.example.android24;

import android.util.Log;

import java.util.*;
import java.io.Serializable;

public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    private final ArrayList<Picture> pictures;
    private String name;
    private static final String TAG = "Album";

    public Album(String name) {
        this.name = name;
        this.pictures = new ArrayList<Picture>();
    }

    public String getName() {
        return this.name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void addPicture(Picture p) {
        this.pictures.add(p);
    }

//    public void removePicture(Picture p) {
//        boolean removed = this.pictures.remove(p);
//        Log.d(TAG, "Picture removed from album '" + this.name + "': " + removed);
//
//        boolean albumRemoved = p.getAlbums().remove(this);
//        Log.d(TAG, "Album '" + this.name + "' removed from picture's album list: " + albumRemoved);
//
//        ArrayList<Album> albums = p.getAlbums();
//        for (int i = 0; i < albums.size(); i++) {
//            Log.d(TAG, "Album " + (i+1) + " of selected photo: " + albums.get(i).getName());
//        }
//    }

    public void removePicture(Picture p) {
        this.pictures.remove(p);
        p.getAlbums().remove(this);
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return Objects.equals(name, album.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
