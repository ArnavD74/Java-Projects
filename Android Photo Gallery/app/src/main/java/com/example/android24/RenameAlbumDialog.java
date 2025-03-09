package com.example.android24;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class RenameAlbumDialog extends Dialog {

    private final EditText etAlbumName;
    private final Button btnRename;

    public RenameAlbumDialog(@NonNull Context context, final Album album, final OnAlbumRenamedListener listener) {
        super(context);
        setContentView(R.layout.dialog_rename_album);

        etAlbumName = findViewById(R.id.etAlbumName);
        btnRename = findViewById(R.id.btnRename);

        etAlbumName.setText(album.getName());

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etAlbumName.getText().toString().trim();
                if (!newName.isEmpty()) {
                    if (listener.onAlbumRenameRequested(newName, album)) {
                        listener.onAlbumRenamed(album, newName);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Album with the same name already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public interface OnAlbumRenamedListener {
        boolean onAlbumRenameRequested(String newName, Album album);
        void onAlbumRenamed(Album album, String newName);
    }
}