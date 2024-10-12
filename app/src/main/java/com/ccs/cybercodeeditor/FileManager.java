package com.ccs.cybercodeeditor;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private Context context;
    private RecyclerView fileRecyclerView;
    private EditText codeEditText;
    private AutosaveManager autosaveManager;

    public FileManager(Context context, RecyclerView fileRecyclerView,
                       EditText codeEditText, AutosaveManager autosaveManager) {
        this.context = context;
        this.fileRecyclerView = fileRecyclerView;
        this.codeEditText = codeEditText;
        this.autosaveManager = autosaveManager;
    }

    public void loadFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        List<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() || file.getName().endsWith(".java")) {
                    fileList.add(file);
                }
            }
            // Оновлений обробник натискання на файл
            FileAdapter adapter = new FileAdapter(fileList, new FileAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(File file) {
                    if (file.isDirectory()) {
                        loadFiles(file.getAbsolutePath());
                    } else {
                        ((MainActivity) context).openFile();
                    }
                }
            });

            fileRecyclerView.setAdapter(adapter);
        }

        FileAdapter adapter = new FileAdapter(fileList, new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File file) {
                if (file.isDirectory()) {
                    loadFiles(file.getAbsolutePath());
                } else {
                    FileOpener fileOpener = new FileOpener(codeEditText, autosaveManager);
                    fileOpener.openFile(Uri.fromFile(file));
                }
            }
        });

        fileRecyclerView.setAdapter(adapter);
    }

}