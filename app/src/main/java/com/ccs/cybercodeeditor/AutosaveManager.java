package com.ccs.cybercodeeditor;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AutosaveManager {

    private Context context;
    private Handler handler;
    private Runnable autosaveRunnable;
    private static final int AUTOSAVE_INTERVAL = 5000; // 1 секунда

    public AutosaveManager(Context context) {
        this.context = context;
        this.handler = new Handler();
    }

    public void startAutosave(final Uri uri, final EditText codeEditText) {
        stopAutosave();

        autosaveRunnable = new Runnable() {
            @Override
            public void run() {
                saveFile(uri, codeEditText.getText().toString());
                handler.postDelayed(this, AUTOSAVE_INTERVAL);
            }
        };

        handler.postDelayed(autosaveRunnable, AUTOSAVE_INTERVAL);
    }

    public void stopAutosave() {
        if (autosaveRunnable != null) {
            handler.removeCallbacks(autosaveRunnable);
        }
    }

    private void saveFile(Uri uri, String content) {
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri, "wt");
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            Toast.makeText(context, "Файл автоматично збережено", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}