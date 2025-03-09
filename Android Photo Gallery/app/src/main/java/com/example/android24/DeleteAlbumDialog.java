package com.example.android24;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteAlbumDialog extends Dialog {
    private EditText editTextAlbumName;
    private final DeleteAlbumListener listener;

    public interface DeleteAlbumListener {
        boolean deleteAlbum(String name);
    }

    public DeleteAlbumDialog(Context context, DeleteAlbumListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_album);

        editTextAlbumName = findViewById(R.id.editTextAlbumName);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String albumName = editTextAlbumName.getText().toString().trim();
                if (listener.deleteAlbum(albumName)) {
                    Toast.makeText(getContext(), "Album deleted successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Album not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
