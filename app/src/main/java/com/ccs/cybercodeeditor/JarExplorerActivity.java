package com.ccs.cybercodeeditor;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class JarExplorerActivity extends AppCompatActivity {

    private ListView classListView;
    private Uri jarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jar_explorer);

        classListView = findViewById(R.id.classListView);

        jarUri = getIntent().getData();
        if (jarUri == null) {
            Toast.makeText(this, "JAR-файл не знайдено", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        JarFileManager jarFileManager = new JarFileManager(this);
        jarFileManager.openJarFile(jarUri, classFiles -> {
            if (classFiles != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, classFiles);
                classListView.setAdapter(adapter);

                classListView.setOnItemClickListener((parent, view, position, id) -> {
                    String classFileName = classFiles.get(position);
                    // Відкриваємо байт-редактор для вибраного файлу
                    Intent intent = new Intent(this, ByteEditorActivity.class);
                    intent.setData(jarUri);
                    intent.putExtra("classFileName", classFileName);
                    startActivity(intent);
                });
            }
        });
    }
}