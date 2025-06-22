package com.example.appmapscamera.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;

import java.util.List;

/**
 * ViewModel responsável por gerenciar e fornecer a lista de locais para a UI.
 * Atua como intermediário entre o repositório de dados e a camada de apresentação (Fragment).
 */
public class HomeViewModel extends ViewModel {

    /**
     * LiveData mutável que contém a lista de locais.
     * Observada pela UI (ex: Fragment) para atualizar a interface automaticamente quando os dados mudam.
     */
    private final MutableLiveData<List<Local>> locais = new MutableLiveData<>();

    /**
     * Retorna a lista de locais encapsulada como LiveData (imutável).
     * A UI pode observar essa LiveData para reagir a mudanças, mas não pode modificá-la diretamente.
     * @return LiveData com a lista de objetos Local.
     */
    public LiveData<List<Local>> getLocais() {
        return locais;
    }

    /**
     * Carrega os locais disponíveis a partir do repositório e os publica na LiveData.
     * Essa chamada aciona os observers da LiveData, como o Fragment que está observando.
     */
    public void carregarLocais() {
        // Busca os locais do repositório e atualiza o valor da LiveData
        locais.setValue(LocalRepository.getTodosLocais());
    }
}

