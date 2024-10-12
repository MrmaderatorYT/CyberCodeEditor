package com.ccs.cybercodeeditor;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarFileManager {

    private Context context;

    public JarFileManager(Context context) {
        this.context = context;
    }

    public void openJarFile(Uri uri, JarFileCallback callback) {
        new Thread(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                JarInputStream jarInputStream = new JarInputStream(inputStream);
                JarEntry jarEntry;

                List<String> classFiles = new ArrayList<>();

                while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                    if (jarEntry.getName().endsWith(".class")) {
                        classFiles.add(jarEntry.getName());
                    }
                }

                jarInputStream.close();

                // Повертаємо результат на головний потік
                new Handler(context.getMainLooper()).post(() -> callback.onJarFileLoaded(classFiles));

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(context.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Помилка при відкритті JAR-файлу", Toast.LENGTH_SHORT).show();
                    callback.onJarFileLoaded(null);
                });
            }
        }).start();
    }

    public interface JarFileCallback {
        void onJarFileLoaded(List<String> classFiles);
    }
}