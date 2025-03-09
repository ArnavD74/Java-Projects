package com.example.android24;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class StorageUtil {

    private static final String TAG = "StorageUtil";
    private static final String ALBUM_DIRECTORY = "albums";

    public static void saveAlbums(Context context, ArrayList<Album> albums) {
        try {
            File albumDirectory = new File(context.getFilesDir(), ALBUM_DIRECTORY);
            if (!albumDirectory.exists()) {
                if (!albumDirectory.mkdir()) {
                    Log.e(TAG, "Failed to create album directory");
                    return;
                }
            }

            File file = new File(albumDirectory, "albums.ser");
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(albums);
            out.close();
            fileOut.close();
            //Log.d(TAG, "Albums saved successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error saving albums", e);
        }
    }

    public static ArrayList<Album> loadAlbums(Context context) {
        ArrayList<Album> albums = new ArrayList<>();
        try {
            File albumDirectory = new File(context.getFilesDir(), ALBUM_DIRECTORY);
            if (!albumDirectory.exists()) {
                Log.d(TAG, "Album directory does not exist");
                return albums;
            }

            File file = new File(albumDirectory, "albums.ser");
            if (!file.exists()) {
                Log.d(TAG, "Albums file does not exist");
                return albums;
            }

            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            albums = (ArrayList<Album>) in.readObject();
            in.close();
            fileIn.close();
            Log.d(TAG, "Albums loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Error loading albums", e);
        }
        return albums;
    }
}