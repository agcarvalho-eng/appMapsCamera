package com.example.appmapscamera.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appmapscamera.R;
import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public MapsFragment() {
        super(R.layout.fragment_maps);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsFragment", "Erro ao carregar o fragmento do mapa.");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mostrarLocais();
    }

    private void mostrarLocais() {
        List<Local> locais = LocalRepository.getTodosLocais();

        if (locais.isEmpty()) {
            Log.d("MapsFragment", "Nenhum local encontrado.");
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (Local local : locais) {
            LatLng pos = new LatLng(local.getLatitude(), local.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(local.getNome())
                    .snippet("Raio: " + local.getRaio() + "m"));

            boundsBuilder.include(pos);
        }

        // Ajusta o zoom para mostrar todos os pontos
        mMap.setOnMapLoadedCallback(() -> {
            try {
                LatLngBounds bounds = boundsBuilder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 450));
            } catch (Exception e) {
                Log.e("MapsFragment", "Erro ao ajustar o zoom: " + e.getMessage());
            }
        });
    }
}

