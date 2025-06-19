package com.example.appmapscamera.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmapscamera.R;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {

    private final List<String> fotos;

    public FotoAdapter(List<String> fotos) {
        this.fotos = fotos != null ? new ArrayList<>(fotos) : new ArrayList<>();
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(fotos.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public void atualizarLista(List<String> novaLista) {
        fotos.clear();
        fotos.addAll(novaLista);
        notifyDataSetChanged();
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.foto_image_view);
        }
    }

    public void atualizarFotos(List<String> novasFotos) {
        fotos.clear();
        fotos.addAll(novasFotos);
        notifyDataSetChanged();
    }

}
