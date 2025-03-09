package com.example.android24;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddAlbumDialog extends Dialog {
    private static final String TAG = "AddAlbumDialog";
    private EditText editTextAlbumName;
    private PhotosAdapter photosAdapter;
    private ArrayList<String> selectedPhotoPaths;
    private final OnAlbumCreatedListener callback;
    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<Intent> photoPickerLauncher;

    public interface OnAlbumCreatedListener {
        boolean onAlbumCreateRequested(String name, ArrayList<String> selectedPhotoPaths);

        void onAlbumCreated(Album album);
    }

    public AddAlbumDialog(Activity activity, OnAlbumCreatedListener listener, ActivityResultRegistry registry) {
        super(activity);
        this.callback = listener;
        this.registry = registry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_album);

        editTextAlbumName = findViewById(R.id.editTextAlbumName);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSelectPhotos = findViewById(R.id.btnSelectPhotos);

        photosAdapter = new PhotosAdapter(getContext(), new ArrayList<>(), false);

        photoPickerLauncher = registry.register("photoPicker", photoPickerContract, this::setSelectedPhotoPaths);

        btnSelectPhotos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            String albumName = editTextAlbumName.getText().toString().trim();
            if (!albumName.isEmpty() && selectedPhotoPaths != null && callback.onAlbumCreateRequested(albumName, selectedPhotoPaths)) {
                Album newAlbum = new Album(albumName);
                for (String photoPath : selectedPhotoPaths) {
                    Picture picture = new Picture(photoPath);
                    newAlbum.addPicture(picture);
                    picture.getAlbums().add(newAlbum);
                }
                callback.onAlbumCreated(newAlbum);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter a unique album name and select photos.", Toast.LENGTH_LONG).show();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private final ActivityResultContract<Intent, ArrayList<String>> photoPickerContract =
            new ActivityResultContract<Intent, ArrayList<String>>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, Intent input) {
                    return input;
                }

                @Override
                public ArrayList<String> parseResult(int resultCode, @Nullable Intent data) {
                    ArrayList<String> selectedPhotoPaths = new ArrayList<>();
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        if (data.getData() != null) {
                            selectedPhotoPaths.add(getPathFromUri(data.getData()));
                        } else if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                selectedPhotoPaths.add(getPathFromUri(data.getClipData().getItemAt(i).getUri()));
                            }
                        }
                    }
                    return selectedPhotoPaths;
                }

                private String getPathFromUri(Uri uri) {
                    String filePath = null;
                    String[] projection = {MediaStore.Images.Media.DATA};

                    try (Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null)) {
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
            };

    void setSelectedPhotoPaths(ArrayList<String> selectedPhotoPaths) {
        this.selectedPhotoPaths = selectedPhotoPaths;
        photosAdapter.setPhotoPaths(this.selectedPhotoPaths);
        if (selectedPhotoPaths != null) {
            Log.d(TAG, "Selected photo paths: " + selectedPhotoPaths);
        } else {
            Log.e(TAG, "Selected photo paths are null");
        }
    }
}