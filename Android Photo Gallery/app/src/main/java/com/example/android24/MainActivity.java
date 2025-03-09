package com.example.android24;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DeleteAlbumDialog.DeleteAlbumListener {

    private static final String TAG = "MainActivity";
    private static ArrayList<Album> albums;
    private ArrayAdapter<String> adapter;
    private static final int REQUEST_CODE_READ_MEDIA_IMAGES = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_MEDIA_IMAGES);
        }

        GridView gridView = findViewById(R.id.gridView);
        Button btnAddAlbum = findViewById(R.id.btnAddAlbum);
        Button btnDeleteAlbum = findViewById(R.id.btnDeleteAlbum);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums = StorageUtil.loadAlbums(this);
        Log.d(TAG, "onCreate: Number of albums loaded: " + albums.size());

        adapter = new ArrayAdapter<>(this, R.layout.item_album, R.id.tvAlbumName, getAlbumNames());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album selectedAlbum = albums.get(position);
                Intent intent = new Intent(MainActivity.this, AlbumDetailsActivity.class);
                intent.putExtra("album", selectedAlbum);
                startActivityForResult(intent, REQUEST_CODE_ALBUM_DETAILS);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Album selectedAlbum = albums.get(position);
                showRenameAlbumDialog(selectedAlbum);
                return true;
            }
        });

        btnAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAlbumDialog();
            }
        });

        btnDeleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlbumDialog();
            }
        });
    }

    private void showAddAlbumDialog() {
        AddAlbumDialog dialog = new AddAlbumDialog(this, new AddAlbumDialog.OnAlbumCreatedListener() {
            @Override
            public boolean onAlbumCreateRequested(String name, ArrayList<String> selectedPhotoPaths) {
                for (Album album : albums) {
                    if (album.getName().equalsIgnoreCase(name)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void onAlbumCreated(Album album) {
                albums.add(album);
                StorageUtil.saveAlbums(MainActivity.this, albums);
                Log.d(TAG, "onAlbumCreated: Album created and saved successfully");
                adapter.clear();
                adapter.addAll(getAlbumNames());
                adapter.notifyDataSetChanged();
            }
        }, getActivityResultRegistry());
        dialog.show();
    }

    private void showDeleteAlbumDialog() {
        DeleteAlbumDialog dialog = new DeleteAlbumDialog(this, this);
        dialog.show();
    }

    @Override
    public boolean deleteAlbum(String name) {
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(name)) {
                albums.remove(i);
                StorageUtil.saveAlbums(MainActivity.this, albums);
                Log.d(TAG, "deleteAlbum: Album deleted and changes saved successfully");
                adapter.clear();
                adapter.addAll(getAlbumNames());
                adapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied to read your Media images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<String> getAlbumNames() {
        ArrayList<String> albumNames = new ArrayList<>();
        for (Album album : albums) {
            albumNames.add(album.getName());
        }
        return albumNames;
    }

    static ArrayList<Album> getAlbums() {
        return albums;
    }

    private static final int REQUEST_CODE_ALBUM_DETAILS = 1001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM_DETAILS && resultCode == RESULT_OK && data != null) {
            Album updatedAlbum = (Album) data.getSerializableExtra("updatedAlbum");
            if (updatedAlbum != null) {
                for (int i = 0; i < albums.size(); i++) {
                    if (albums.get(i).getName().equals(updatedAlbum.getName())) {
                        albums.set(i, updatedAlbum);
                        break;
                    }
                }
                StorageUtil.saveAlbums(MainActivity.this, albums);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static void setAlbums(ArrayList<Album> updatedAlbums) {
        albums = updatedAlbums;
    }

    public static Album getSpecificAlbum(String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    public static void updateAlbum(Context activityContext, Album updatedAlbum) {
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(updatedAlbum.getName())) {
                albums.set(i, updatedAlbum);
                break;
            }
        }
        StorageUtil.saveAlbums(activityContext, albums);
    }

    private void showRenameAlbumDialog(final Album album) {
        RenameAlbumDialog dialog = new RenameAlbumDialog(this, album, new RenameAlbumDialog.OnAlbumRenamedListener() {
            @Override
            public boolean onAlbumRenameRequested(String newName, Album album) {
                for (Album existingAlbum : albums) {
                    if (existingAlbum.getName().equalsIgnoreCase(newName)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void onAlbumRenamed(Album album, String newName) {
                Album renamedAlbum = new Album(newName);
                for (Picture picture : album.getPictures()) {
                    renamedAlbum.addPicture(picture);
                    picture.getAlbums().remove(album);
                    picture.getAlbums().add(renamedAlbum);
                }
                albums.remove(album);
                albums.add(renamedAlbum);
                StorageUtil.saveAlbums(MainActivity.this, albums);
                Log.d(TAG, "onAlbumRenamed: Album renamed and saved successfully");
                adapter.clear();
                adapter.addAll(getAlbumNames());
                adapter.notifyDataSetChanged();
            }

        });
        dialog.show();
    }
}