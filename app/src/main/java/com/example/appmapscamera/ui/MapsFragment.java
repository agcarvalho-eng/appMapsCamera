package com.example.appmapscamera.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentMapsBinding;
import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.viewModel.MapsViewModel;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.List;

/**
 * Fragmento responsável por exibir os locais cadastrados no mapa.
 * Observa os dados fornecidos por {@link MapsViewModel}.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapsViewModel viewModel;
    private FragmentMapsBinding binding;
    //private FragmentMapsBinding binding;

    /**
     * Construtor que define o layout do fragmento como {@code fragment_maps}.
     */
    public MapsFragment() {
        super(R.layout.fragment_maps);
    }

    /**
     * Infla o layout do fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inicializa o binding e infla o layout usando o root da view
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false);
        return binding.getRoot();
    }

    /**
     * Inicializa o mapa e o ViewModel após a view ser criada.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Chama a implementação da superclasse para garantir que o ciclo de vida do fragmento continue corretamente
        super.onViewCreated(view, savedInstanceState);

        // Inicializa o ViewModel associado a este fragmento
        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);

        // Conecta o ViewModel ao layout por meio do Data Binding
        binding.setViewModel(viewModel);

        // Define o ciclo de vida do binding para que ele observe corretamente os LiveData
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Recupera o fragmento de mapa declarado no XML pelo seu ID
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        // Se o fragmento de mapa foi encontrado corretamente...
        if (mapFragment != null) {
            // ...solicita para ser notificado quando o mapa estiver pronto para uso
            mapFragment.getMapAsync(this);
        } else {
            // Caso contrário, registra um erro no log
            Log.e("MapsFragment", "Erro ao carregar o fragmento do mapa.");
        }
    }



    /**
     * Callback chamado quando o mapa estiver pronto.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Observa os locais e os mostra no mapa
        viewModel.getLocais().observe(getViewLifecycleOwner(), this::mostrarLocais);
        viewModel.carregarLocais();
    }

    /**
     * Exibe marcadores no mapa com base em uma lista de locais.
     * Cada local é representado por um marcador com nome e raio.
     * O zoom é ajustado automaticamente para exibir todos os marcadores na tela.
     *
     * @param locais Lista de objetos Local a serem exibidos no mapa.
     */
    private void mostrarLocais(List<Local> locais) {
        // Verifica se a lista está vazia ou nula e encerra o método se não houver dados.
        if (locais == null || locais.isEmpty()) {
            Log.d("MapsFragment", "Nenhum local encontrado.");
            return;
        }

        // Cria um construtor de limites geográficos que ajudará a ajustar o zoom do mapa
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        // Itera sobre todos os locais da lista
        for (Local local : locais) {
            // Cria um objeto LatLng com as coordenadas do local atual
            LatLng pos = new LatLng(local.getLatitude(), local.getLongitude());

            // Adiciona um marcador no mapa para o local atual, com título e raio no snippet
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(local.getNome())
                    .snippet("Raio: " + local.getRaio() + "m"));

            // Inclui esta posição no construtor de limites
            boundsBuilder.include(pos);
        }

        // Aguarda o carregamento completo do mapa antes de ajustar o zoom
        mMap.setOnMapLoadedCallback(() -> {
            try {
                // Constrói os limites finais que englobam todos os pontos adicionados
                LatLngBounds bounds = boundsBuilder.build();

                // Move a câmera do mapa para ajustar automaticamente todos os pontos na tela,
                // com um padding (margem) de 450 pixels
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 450));
            } catch (Exception e) {
                // Em caso de erro ao ajustar os limites ou mover a câmera, registra o erro no log
                Log.e("MapsFragment", "Erro ao ajustar o zoom: " + e.getMessage());
            }
        });
    }

}


