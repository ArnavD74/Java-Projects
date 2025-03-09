package com.example.android24;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;

public class PhotoPreviewActivity extends AppCompatActivity {
    private ImageView imageViewPreview;
    private TextView textViewTagsAndDate;
    private Button buttonEditTags, buttonMoveAlbums;
    private Picture selectedPicture;
    private String currentAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        textViewTagsAndDate = findViewById(R.id.textViewTagsAndDate);
        buttonEditTags = findViewById(R.id.buttonEditTags);
        buttonMoveAlbums = findViewById(R.id.buttonMoveAlbums);

        selectedPicture = (Picture) getIntent().getSerializableExtra("picture");
        currentAlbumName = getIntent().getStringExtra("currentAlbumName");

        if (selectedPicture != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedPicture.getName());
            imageViewPreview.setImageBitmap(bitmap);
            updateTagsDisplay();
        } else {
            Toast.makeText(this, "Error: Picture not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonMoveAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoveAlbumsDialog();
            }
        });

        buttonEditTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTagsDialog();
            }
        });

        updateTagsAndDateDisplay();
    }

    private void updateTagsDisplay() {
        StringBuilder tagBuilder = new StringBuilder();
        for (Tag tag : selectedPicture.getTags()) {
            tagBuilder.append(tag.getName()).append(": ");
            tagBuilder.append(String.join(", ", tag.getValues()));
            tagBuilder.append("\n");
        }
        textViewTagsAndDate.setText(tagBuilder.toString());
    }

    private void showEditTagsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_tags, null);
        CheckBox checkBoxPeople = dialogView.findViewById(R.id.checkBoxPeople);
        CheckBox checkBoxLocation = dialogView.findViewById(R.id.checkBoxLocation);
        EditText editTextPeople = dialogView.findViewById(R.id.editTextPeople);
        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);

        for (Tag tag : selectedPicture.getTags()) {
            if (tag.getTagType().equals("people")) {
                checkBoxPeople.setChecked(true);
                editTextPeople.setText(String.join(", ", tag.getValues()));
            } else if (tag.getTagType().equals("location")) {
                checkBoxLocation.setChecked(true);
                editTextLocation.setText(tag.getValues().get(0));
            }
        }

        builder.setView(dialogView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPicture.getTags().clear();

                if (checkBoxPeople.isChecked()) {
                    String peopleTagValue = editTextPeople.getText().toString().trim();
                    if (!peopleTagValue.isEmpty()) {
                        Tag peopleTag = new Tag("People", true, "people");
                        String[] peopleValues = peopleTagValue.split(",");
                        for (String value : peopleValues) {
                            peopleTag.addValue(value.trim());
                        }
                        selectedPicture.addTag(peopleTag);
                    }
                }

                if (checkBoxLocation.isChecked()) {
                    String locationTagValue = editTextLocation.getText().toString().trim();
                    if (!locationTagValue.isEmpty()) {
                        Tag locationTag = new Tag("Location", false, "location");
                        locationTag.addValue(locationTagValue);
                        selectedPicture.addTag(locationTag);
                    }
                }

                updateTagsDisplay();
                updateTagsAndDateDisplay();
                saveData();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMoveAlbumsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to Album");

        ArrayList<Album> albums = MainActivity.getAlbums();
        ArrayList<Album> filteredAlbums = new ArrayList<>();
        String currentAlbumName = "";

        if (!selectedPicture.getAlbums().isEmpty()) {
            currentAlbumName = selectedPicture.getAlbums().get(0).getName();
        }

        for (Album album : albums) {
            if (!album.getName().equals(currentAlbumName)) {
                filteredAlbums.add(album);
            }
        }

        String[] albumNames = new String[filteredAlbums.size()];
        for (int i = 0; i < filteredAlbums.size(); i++) {
            albumNames[i] = filteredAlbums.get(i).getName();
        }

        builder.setItems(albumNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Album selectedAlbum = filteredAlbums.get(which);
                movePictureToAlbum(selectedAlbum);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static final String TAG = "PhotoPreviewActivity";

    private void movePictureToAlbum(Album destinationAlbum) {
        Album currentAlbum = MainActivity.getSpecificAlbum(currentAlbumName);
        if (currentAlbum != null) {
            Log.d("PhotoPreviewActivity", "Current album name: " + currentAlbum.getName());
            Log.d("PhotoPreviewActivity", "Destination album name: " + destinationAlbum.getName());

            currentAlbum.removePicture(selectedPicture);
            selectedPicture.getAlbums().remove(currentAlbum);

            destinationAlbum.addPicture(selectedPicture);
            selectedPicture.getAlbums().add(destinationAlbum);

            MainActivity.updateAlbum(this, destinationAlbum);
            MainActivity.updateAlbum(this, currentAlbum);

            ArrayList<Album> albums = selectedPicture.getAlbums();
            for (int i = 0; i < albums.size(); i++) {
                Log.d("PhotoPreviewActivity", "Album " + (i + 1) + " of selected photo: " + albums.get(i).getName());
            }

            saveData();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPicture", selectedPicture);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void updateTagsAndDateDisplay() {
        TextView textViewTagsAndDate = findViewById(R.id.textViewTagsAndDate);
        StringBuilder tagBuilder = new StringBuilder();

        if (selectedPicture.getTags().isEmpty()) {
            tagBuilder.append("<i><font color='#888888'>No tags</font></i>");
        } else {
            tagBuilder.append("Tags: ");
            for (Tag tag : selectedPicture.getTags()) {
                tagBuilder.append(tag.getName()).append(": ");
                tagBuilder.append(String.join(", ", tag.getValues()));
                tagBuilder.append(", ");
            }
            tagBuilder.setLength(tagBuilder.length() - 2);
        }

        tagBuilder.append(" • ");
        File file = new File(selectedPicture.getName());
        long lastModified = file.lastModified();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(lastModified));
        tagBuilder.append(formattedDate);

        textViewTagsAndDate.setText(Html.fromHtml(tagBuilder.toString()));
    }

    private void saveData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedPicture", selectedPicture);
        setResult(RESULT_OK, resultIntent);
        StorageUtil.saveAlbums(this, MainActivity.getAlbums());
        finish();
    }
}