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

/**
 * Adaptador para exibir caminhos de imagens em um RecyclerView.
 * Utiliza Glide para carregar imagens de forma assíncrona e eficiente.
 */
public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {

    /** Lista de caminhos de imagens a exibir */
    private final List<String> fotos;

    /**
     * Construtor do adaptador.
     * @param fotos Lista inicial de caminhos de imagens.
     */
    public FotoAdapter(List<String> fotos) {
        this.fotos = fotos != null ? new ArrayList<>(fotos) : new ArrayList<>();
    }

    /**
     * Infla o layout do item de foto (item_foto.xml).
     * @param parent ViewGroup pai.
     * @param viewType Tipo de view (não utilizado).
     * @return Novo ViewHolder configurado.
     */
    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    /**
     * Vincula a imagem ao ViewHolder usando Glide.
     * @param holder ViewHolder a ser populado.
     * @param position Posição do item na lista.
     */
    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(fotos.get(position))
                .into(holder.imageView);
    }

    /**
     * Retorna a quantidade de itens na lista.
     * @return Número de imagens no adaptador.
     */
    @Override
    public int getItemCount() {
        return fotos.size();
    }

    /**
     * ViewHolder contendo referência à ImageView exibindo a foto.
     */
    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Construtor que associa a ImageView do item.
         * @param itemView View raiz do layout item_foto.xml.
         */
        FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.foto_image_view);
        }
    }

    /**
     * Atualiza a lista de imagens e notifica o RecyclerView.
     * @param novasFotos Novos caminhos de imagens para exibição.
     */
    public void atualizarFotos(List<String> novasFotos) {
        fotos.clear();
        fotos.addAll(novasFotos);
        notifyDataSetChanged();
    }
}


