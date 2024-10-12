package com.ccs.cybercodeeditor;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ByteEditorActivity extends AppCompatActivity {

    private EditText byteEditText;
    private Uri jarUri;
    private String classFileName;
    private byte[] classFileBytes;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_byte_editor);

        byteEditText = findViewById(R.id.byteEditText);

        jarUri = getIntent().getData();
        classFileName = getIntent().getStringExtra("classFileName");

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveChanges());

        if (jarUri == null || classFileName == null) {
            Toast.makeText(this, "Дані для редагування відсутні", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadClassFileBytes();
    }

    private void loadClassFileBytes() {
        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(jarUri);
                JarInputStream jarInputStream = new JarInputStream(inputStream);
                JarEntry jarEntry;

                while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                    if (jarEntry.getName().equals(classFileName)) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }

                        classFileBytes = baos.toByteArray();
                        break;
                    }
                }

                jarInputStream.close();

                if (classFileBytes != null) {
                    runOnUiThread(() -> byteEditText.setText(bytesToHex(classFileBytes)));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Не вдалося завантажити файл", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Помилка при читанні файлу", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
    private void saveChanges() {
        String hexString = byteEditText.getText().toString().replaceAll("\\s+", "");
        byte[] newBytes = hexStringToByteArray(hexString);

        if (newBytes != null) {
            // Тут можна реалізувати збереження змінених байтів назад у JAR-файл
            // Це досить складний процес і потребує обережності

            Toast.makeText(this, "Зміни збережено (імітація)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Помилка при конвертації байтів", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] hexStringToByteArray(String s) {
        try {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for(int i = 0; i < len; i += 2){
                data[i / 2] = (byte)((Character.digit(s.charAt(i),16)<<4)+Character.digit(s.charAt(i+1),16));
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}