package com.example.appmapscamera.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appmapscamera.repository.FotoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class AdicionarFotoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> fotos = new MutableLiveData<>(new ArrayList<>());

    public AdicionarFotoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<String>> getFotos() {
        return fotos;
    }

    /**
     * Carrega as fotos salvas associadas a um local.
     */
    public void carregarFotos(Context context, String localName) {
        List<String> lista = FotoRepository.getTodasFotos(context, localName);
        fotos.setValue(lista != null ? lista : new ArrayList<>());
    }

    /**
     * Salva uma nova foto usando o FotoRepository.
     */
    public void salvarFoto(Context context,
                           String localName,
                           ImageCapture imageCapture,
                           Executor executor,
                           ImageCapture.OnImageSavedCallback callback) {
        FotoRepository.salvarFoto(context, localName, imageCapture, executor, callback);
    }

    /**
     * Adiciona uma nova foto ao LiveData local após ser salva fisicamente.
     */
    public void adicionarFoto(String caminhoFoto) {
        List<String> listaAtual = fotos.getValue();
        if (listaAtual == null) listaAtual = new ArrayList<>();
        listaAtual.add(caminhoFoto);
        fotos.setValue(new ArrayList<>(listaAtual)); // força atualização
    }
}

