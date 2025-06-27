package com.example.appmapscamera.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appmapscamera.repository.FotoRepository;

import java.util.List;

public class ListaDeFotosViewModel extends ViewModel {

    private final MutableLiveData<List<String>> fotos = new MutableLiveData<>();

    public LiveData<List<String>> getFotos() {
        return fotos;
    }

    public void setNomeLocal(Context context, String nomeLocal) {
        List<String> lista = FotoRepository.getTodasFotos(context, nomeLocal);
        fotos.setValue(lista);
    }
}

