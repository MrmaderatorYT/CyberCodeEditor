package com.ccs.cybercodeeditor;

import android.content.ContentResolver;
import android.net.Uri;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileOpener {

    private EditText codeEditText;
    private AutosaveManager autosaveManager;

    public FileOpener(EditText codeEditText, AutosaveManager autosaveManager) {
        this.codeEditText = codeEditText;
        this.autosaveManager = autosaveManager;
    }

    public void openFile(Uri uri) {
        new Thread(() -> {
            try {
                ContentResolver contentResolver = codeEditText.getContext().getContentResolver();
                InputStream inputStream = contentResolver.openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                reader.close();
                String code = stringBuilder.toString();

                // Оновлення UI на головному потоці
                codeEditText.post(() -> {
                    codeEditText.setText(code);

                    // Запуск автозбереження
                    autosaveManager.startAutosave(uri, codeEditText);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}