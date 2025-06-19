package com.example.appmapscamera.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Local>> locais = new MutableLiveData<>();

    public LiveData<List<Local>> getLocais() {
        return locais;
    }

    public void carregarLocais() {
        locais.setValue(LocalRepository.getTodosLocais());
    }
}
