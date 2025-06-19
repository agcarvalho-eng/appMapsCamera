package com.example.appmapscamera.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentAdicionarLocalizacaoBinding;
import com.example.appmapscamera.model.Local;
import com.example.appmapscamera.repository.LocalRepository;
import com.google.android.gms.location.*;

public class AdicionarLocalizacaoFragment extends Fragment {

    private FragmentAdicionarLocalizacaoBinding binding;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) requestCurrentLocation();
                else Toast.makeText(getContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_adicionar_localizacao, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestLocationPermission();

        binding.buttonSalvar.setOnClickListener(v -> {
            String nome = binding.textNome.getText().toString().trim();
            String raioStr = binding.textRaio.getText().toString().trim();

            if (nome.isEmpty() || raioStr.isEmpty()) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int raio = Integer.parseInt(raioStr);
            Local local = new Local(nome, raio, latitude, longitude);
            LocalRepository.salvarLocal(getContext(), local);

            Bundle bundle = new Bundle();
            bundle.putString("localName", nome);
            Navigation.findNavController(view).navigate(R.id.adicionarFotoFragment, bundle);
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            requestCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // tempo entre atualizações

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    binding.textCoordenadas.setText("Lat: " + latitude + " | Lng: " + longitude);

                    // Interrompe as atualizações após obter a primeira localização
                    locationProviderClient.removeLocationUpdates(locationCallback);
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability availability) {
                if (!availability.isLocationAvailable()) {
                    binding.textCoordenadas.setText("Localização indisponível no momento");
                }
            }
        };

        locationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationCallback != null) {
            locationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}
