package com.ccs.cybercodeeditor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1002;
    private EditText codeEditText;
    private AutosaveManager autosaveManager;
    private ImageButton openFileButton;
    private TextView fileNameTextView;
    private ImageView fileIcon;
    private ImageButton openJarButton;
    private static final int REQUEST_CODE_OPEN_JAR = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація елементів інтерфейсу
        codeEditText = findViewById(R.id.codeEditText);
        openFileButton = findViewById(R.id.openFileButton);
        fileNameTextView = findViewById(R.id.fileNameTextView);
        fileIcon = findViewById(R.id.fileIcon);
        openJarButton = findViewById(R.id.openJarButton);

        // Ініціалізація менеджера автозбереження
        autosaveManager = new AutosaveManager(this);

        // Налаштування синтаксичного підсвічування
        codeEditText.addTextChangedListener(new SyntaxHighlighter(codeEditText));

        // Обробник кнопки відкриття файлу
        openFileButton.setOnClickListener(v -> openFile());
        openJarButton.setOnClickListener(v -> openJarFile());

    }

    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Дозволяємо вибір усіх типів файлів

        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_JAR && resultCode == RESULT_OK) {
            if (data != null) {
                Uri jarUri = data.getData();

                // Збереження дозволів
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(jarUri, takeFlags);

                // Запуск JarExplorerActivity
                Intent intent = new Intent(this, JarExplorerActivity.class);
                intent.setData(jarUri);
                startActivity(intent);
            }
        } else if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                // Збереження дозволів
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                // Отримання імені файлу
                String fileName = getFileName(uri);

                // Перевірка, чи файл має розширення .java
                if (fileName != null && fileName.toLowerCase().endsWith(".java")) {
                    fileNameTextView.setText(fileName);
                    fileNameTextView.setVisibility(View.VISIBLE);
                    fileIcon.setVisibility(View.VISIBLE);

                    // Відкриття файлу
                    FileOpener fileOpener = new FileOpener(codeEditText, autosaveManager);
                    fileOpener.openFile(uri);
                }
                else {
                    Toast.makeText(this, "Будь ласка, виберіть файл Java-класу (.java)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void openJarFile() {
        // Для тестування можна додати повідомлення
        Toast.makeText(this, "Кнопка відкриття JAR натиснута", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/java-archive");
        startActivityForResult(intent, REQUEST_CODE_OPEN_JAR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autosaveManager.stopAutosave();
    }
}