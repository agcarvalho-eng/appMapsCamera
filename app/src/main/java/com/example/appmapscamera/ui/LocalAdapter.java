package com.example.appmapscamera.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmapscamera.databinding.ItemLocalBinding;
import com.example.appmapscamera.model.Local;

import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmapscamera.databinding.ItemLocalBinding;
import com.example.appmapscamera.model.Local;

import java.util.List;

/**
 * Adaptador para exibição de objetos {@link Local} em um RecyclerView.
 * Utiliza Data Binding para associar os dados à interface.
 */
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    /** Lista de locais a serem exibidos no RecyclerView. */
    private final List<Local> localList;

    /** Listener para tratamento de clique nos itens da lista. */
    private final OnItemClickListener listener;

    /**
     * Interface de callback para cliques nos itens da lista.
     */
    public interface OnItemClickListener {
        /**
         * Método chamado quando um item é clicado.
         * @param local O objeto Local clicado.
         */
        void onItemClick(Local local);
    }

    /**
     * Construtor do adaptador.
     * @param localList Lista de objetos Local a ser exibida.
     * @param listener  Listener para tratamento de cliques nos itens.
     */
    public LocalAdapter(List<Local> localList, OnItemClickListener listener) {
        this.localList = localList;
        this.listener = listener;
    }

    /**
     * Cria uma nova instância de ViewHolder com o layout item_local.xml.
     * @param parent ViewGroup pai.
     * @param viewType Tipo da view (não utilizado neste caso).
     * @return Um novo LocalViewHolder.
     */
    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLocalBinding binding = ItemLocalBinding.inflate(inflater, parent, false);
        return new LocalViewHolder(binding);
    }

    /**
     * Associa os dados de um Local ao ViewHolder.
     * @param holder O ViewHolder a ser atualizado.
     * @param position A posição do item na lista.
     */
    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.binding.setLocal(local); // vincula dados ao layout via Data Binding

        // Define o listener de clique no item
        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(local);
        });
    }

    /**
     * Retorna o número de itens na lista.
     * @return Quantidade de itens no adaptador.
     */
    @Override
    public int getItemCount() {
        return localList.size();
    }

    /**
     * ViewHolder que armazena a referência ao binding do layout item_local.
     */
    static class LocalViewHolder extends RecyclerView.ViewHolder {
        final ItemLocalBinding binding;

        /**
         * Construtor do ViewHolder.         * @param binding Binding gerado pelo layout item_local.xml.
         */
        public LocalViewHolder(ItemLocalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
