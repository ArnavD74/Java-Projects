package com.example.android24;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class AlbumDetailsActivity extends AppCompatActivity {

    private Album selectedAlbum;
    private PictureAdapter adapter;
    private boolean deleteMode = false;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PHOTO_PREVIEW_REQUEST = 2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        selectedAlbum = (Album) getIntent().getSerializableExtra("album");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PictureAdapter(this, selectedAlbum.getPictures());
        recyclerView.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnSlideshow = findViewById(R.id.btnSlideshow);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnDelete.setOnClickListener(v -> {
            deleteMode = !deleteMode;
            adapter.setDeleteMode(deleteMode);
            if (deleteMode) {
                Toast.makeText(this, "Click on photos to delete them. Click \"Done\" once complete.", Toast.LENGTH_SHORT).show();
            }
            btnDelete.setText(deleteMode ? "Done" : "Delete");
            if (!deleteMode) {
                saveAlbums();
            }
        });

        btnSlideshow.setOnClickListener(v -> {
            ArrayList<String> imagePaths = new ArrayList<>();
            for (Picture picture : selectedAlbum.getPictures()) {
                imagePaths.add(picture.getName());
            }
            Intent intent = new Intent(AlbumDetailsActivity.this, SlideshowActivity.class);
            intent.putStringArrayListExtra("imagePaths", imagePaths);
            startActivity(intent);
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(View view, int position) {
                Picture selectedPicture = selectedAlbum.getPictures().get(position);
                if (deleteMode) {
                    selectedAlbum.removePicture(selectedPicture);
                    selectedPicture.getAlbums().remove(selectedAlbum);
                    adapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(AlbumDetailsActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("picture", selectedPicture);
                    intent.putExtra("currentAlbumName", selectedAlbum.getName());
                    startActivityForResult(intent, PHOTO_PREVIEW_REQUEST);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }

        }));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String photoPath = getPathFromUri(imageUri);
                    Picture picture = new Picture(photoPath);
                    selectedAlbum.addPicture(picture);
                    picture.getAlbums().add(selectedAlbum);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                String photoPath = getPathFromUri(imageUri);
                Picture picture = new Picture(photoPath);
                selectedAlbum.addPicture(picture);
                picture.getAlbums().add(selectedAlbum);
            }
            saveAlbums();
            adapter.notifyDataSetChanged();
        } else if (requestCode == PHOTO_PREVIEW_REQUEST && resultCode == RESULT_OK && data != null) {
            Picture updatedPicture = (Picture) data.getSerializableExtra("updatedPicture");
            if (updatedPicture != null) {
                int index = selectedAlbum.getPictures().indexOf(updatedPicture);
                if (index != -1) {
                    selectedAlbum.getPictures().set(index, updatedPicture);
                    adapter.notifyDataSetChanged();
                }
            }
            saveAlbums();
        }
    }

    void saveAlbums() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedAlbum", selectedAlbum);
        setResult(RESULT_OK, resultIntent);
        StorageUtil.saveAlbums(this, MainActivity.getAlbums());
        finish();
    }

    private String getPathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (filePath == null) {
            filePath = uri.getPath();
        }

        return filePath;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Album> updatedAlbums = MainActivity.getAlbums();
        for (Album album : updatedAlbums) {
            if (album.getName().equals(selectedAlbum.getName())) {
                selectedAlbum = album;
                adapter = new PictureAdapter(this, selectedAlbum.getPictures());
                recyclerView.setAdapter(adapter);
                break;
            }
        }
    }
}