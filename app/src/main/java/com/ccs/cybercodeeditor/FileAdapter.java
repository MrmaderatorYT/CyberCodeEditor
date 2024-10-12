package com.ccs.cybercodeeditor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    private List<File> files;
    private OnItemClickListener listener;

    public FileAdapter(List<File> files, OnItemClickListener listener) {
        this.files = files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        final File file = files.get(position);
        holder.fileNameTextView.setText(file.getName());

        // Встановлення іконки залежно від типу
        if (file.isDirectory()) {
            holder.fileIcon.setImageResource(R.drawable.ic_folder);
        } else {
            holder.fileIcon.setImageResource(R.drawable.ic_file);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        ImageView fileIcon;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
        }
    }

}