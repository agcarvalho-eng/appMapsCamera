package com.example.appmapscamera.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.LifecycleCameraController;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmapscamera.databinding.FragmentAdicionarFotoBinding;
import com.example.appmapscamera.repository.FotoRepository;
import com.example.appmapscamera.ui.FotoAdapter;
import com.example.appmapscamera.viewModel.AdicionarFotoViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdicionarFotoFragment extends Fragment {

    private static final String TAG = "AdicionarFotoFragment";

    private FragmentAdicionarFotoBinding binding;
    private AdicionarFotoViewModel viewModel;
    private FotoAdapter adapter;
    private LifecycleCameraController cameraController;
    private String nomeLocal;
    private ImageCapture imageCapture;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(requireContext(), "Permissão da câmera negada.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdicionarFotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdicionarFotoViewModel.class);

        // Inicializa RecyclerView com adapter
        binding.recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> listaInicial = viewModel.getFotos().getValue();
        if (listaInicial == null) listaInicial = new ArrayList<>();
        adapter = new FotoAdapter(listaInicial);
        binding.recyclerViewPhotos.setAdapter(adapter);

        if (getArguments() != null) {
            nomeLocal = getArguments().getString("localName");
            viewModel.carregarFotos(requireContext(), nomeLocal);
        }

        viewModel.getFotos().observe(getViewLifecycleOwner(), fotos -> adapter.atualizarFotos(fotos));

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }

        binding.buttonTakePhoto.setOnClickListener(v -> tirarFoto());
    }

    private void startCamera() {
        cameraController = new LifecycleCameraController(requireContext());
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        cameraController.bindToLifecycle(this);
        binding.previewView.setController(cameraController);
        imageCapture = new ImageCapture.Builder().build();
    }

    private void tirarFoto() {
        if (imageCapture == null) return;

        File localDir = FotoRepository.getBaseDir(requireContext(), nomeLocal);
        String nomeArquivo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg";
        File fotoArquivo = new File(localDir, nomeArquivo);

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(fotoArquivo).build();

        cameraController.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Uri uriSalvo = Uri.fromFile(fotoArquivo);
                        viewModel.adicionarFoto(uriSalvo.toString());
                        Toast.makeText(requireContext(), "Foto salva em: " + uriSalvo.getPath(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Erro ao capturar imagem", exception);
                        Toast.makeText(requireContext(), "Erro ao capturar imagem", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
