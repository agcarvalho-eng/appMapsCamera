package com.example.appmapscamera.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;

import java.util.List;

/**
 * ViewModel responsável por fornecer os locais cadastrados para o mapa.
 * Isola a lógica de dados da UI do MapsFragment.
 */
public class MapsViewModel extends ViewModel {

    /** LiveData que armazena e expõe a lista de locais. */
    private final MutableLiveData<List<Local>> locais = new MutableLiveData<>();

    /**
     * Retorna os locais observáveis.
     * @return LiveData com a lista de locais.
     */
    public LiveData<List<Local>> getLocais() {
        return locais;
    }

    /**
     * Carrega os locais do repositório e atualiza o LiveData.
     */
    public void carregarLocais() {
        locais.setValue(LocalRepository.getTodosLocais());
    }
}

