package com.example.appmapscamera.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appmapscamera.repository.FotoRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdicionarFotoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> fotos = new MutableLiveData<>(new ArrayList<>());

    public AdicionarFotoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<String>> getFotos() {
        return fotos;
    }


    public void carregarFotos(Context context, String localName) {
        List<String> lista = FotoRepository.getTodasFotos(context, localName);
        if (lista == null) lista = new ArrayList<>();
        fotos.setValue(lista);
    }

    public void adicionarFoto(String caminhoFoto) {
        List<String> listaAtual = fotos.getValue();
        if (listaAtual == null) listaAtual = new ArrayList<>();
        listaAtual.add(caminhoFoto);
        fotos.setValue(new ArrayList<>(listaAtual)); // força atualização
    }

}