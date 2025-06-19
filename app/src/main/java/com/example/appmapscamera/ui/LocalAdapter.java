package com.example.appmapscamera.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmapscamera.databinding.ItemLocalBinding;
import com.example.appmapscamera.model.Local;

import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {
    private final List<Local> localList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Local local);
    }

    public LocalAdapter(List<Local> localList, OnItemClickListener listener) {
        this.localList = localList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLocalBinding binding = ItemLocalBinding.inflate(inflater, parent, false);
        return new LocalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.binding.setLocal(local);
        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(local);
        });
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        final ItemLocalBinding binding;

        public LocalViewHolder(ItemLocalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}