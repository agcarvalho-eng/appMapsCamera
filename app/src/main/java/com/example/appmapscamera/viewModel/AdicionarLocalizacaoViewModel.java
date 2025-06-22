package com.example.appmapscamera.viewModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;
import com.google.android.gms.location.*;

/**
 * ViewModel responsável por gerenciar a lógica de localização e cadastro de um novo local.
 * Atua como intermediário entre a interface (Fragment) e as camadas de dados (Repository e sistema de localização).
 */
public class AdicionarLocalizacaoViewModel extends AndroidViewModel {

    /**
     * Cliente para acessar a API de localização do Google.
     */
    private final FusedLocationProviderClient locationProviderClient;

    /**
     * Callback chamado quando a localização é atualizada.
     */
    private LocationCallback locationCallback;

    /**
     * LiveData que contém a latitude e longitude atual.
     */
    private final MutableLiveData<Location> coordenadas = new MutableLiveData<>();

    /**
     * LiveData que representa mensagens de erro ou sucesso que devem ser exibidas na interface.
     */
    private final MutableLiveData<String> mensagem = new MutableLiveData<>();

    public AdicionarLocalizacaoViewModel(@NonNull Application application) {
        super(application);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(application);
    }

    /**
     * Retorna as coordenadas atuais da localização.
     *
     * @return LiveData contendo a localização (latitude e longitude).
     */
    public LiveData<Location> getCoordenadas() {
        return coordenadas;
    }

    /**
     * Retorna mensagens informativas para exibição na UI.
     *
     * @return LiveData contendo mensagens de status.
     */
    public LiveData<String> getMensagem() {
        return mensagem;
    }

    /**
     * Verifica e solicita atualizações da localização se a permissão for concedida.
     */
    @SuppressLint("MissingPermission")
    public void iniciarLocalizacao() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location ultima = locationResult.getLastLocation();
                if (ultima != null) {
                    coordenadas.setValue(ultima);
                    // Parar as atualizações após obter uma localização
                    locationProviderClient.removeLocationUpdates(locationCallback);
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability availability) {
                if (!availability.isLocationAvailable()) {
                    mensagem.setValue("Localização indisponível no momento");
                }
            }
        };

        locationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    /**
     * Cancela as atualizações de localização, geralmente chamado ao destruir a view.
     */
    public void pararLocalizacao() {
        if (locationCallback != null) {
            locationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Realiza a validação dos campos e salva o novo local no repositório.
     *
     * @param nome     Nome fornecido pelo usuário.
     * @param raioStr  Raio fornecido pelo usuário em forma de texto.
     */
    public void salvarLocal(String nome, String raioStr) {
        if (nome.isEmpty() || raioStr.isEmpty()) {
            mensagem.setValue("Preencha todos os campos");
            return;
        }

        int raio;
        try {
            raio = Integer.parseInt(raioStr);
        } catch (NumberFormatException e) {
            mensagem.setValue("Raio inválido");
            return;
        }

        Location loc = coordenadas.getValue();
        if (loc == null) {
            mensagem.setValue("Localização ainda não disponível");
            return;
        }

        Local local = new Local(nome, raio, loc.getLatitude(), loc.getLongitude());
        LocalRepository.salvarLocal(getApplication().getApplicationContext(), local);
        mensagem.setValue("salvo:" + nome); // formato para navegação ser interpretada
    }
}
