package com.example.appmapscamera.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentAdicionarFotoBinding;
import com.example.appmapscamera.viewModel.AdicionarFotoViewModel;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Fragmento responsável por capturar fotos usando a câmera e associá-las a um local específico.
 * Utiliza o ViewModel para gerenciar os dados e o Repository para persistência.
 */
public class AdicionarFotoFragment extends Fragment {

    private FragmentAdicionarFotoBinding binding;
    private AdicionarFotoViewModel viewModel;
    private FotoAdapter adapter;
    private ImageCapture imageCapture;
    private String nomeLocal;

    /**
     * Launcher para solicitação de permissão de uso da câmera.
     * Inicia a câmera caso a permissão seja concedida.
     */
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) iniciarCamera();
                else Toast.makeText(requireContext(), "Permissão da câmera negada.", Toast.LENGTH_SHORT).show();
            });

    /**
     * Define que o fragmento possui seu próprio menu de opções (Toolbar),
     * permitindo interceptar eventos como o clique no botão de navegação (seta voltar).
     *
     * @param savedInstanceState Estado anterior da instância
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Habilita o uso do menu no fragmento
    }

    /**
     * Infla o layout XML do fragmento usando DataBinding.
     *
     * @param inflater  LayoutInflater
     * @param container ViewGroup pai
     * @param savedInstanceState Bundle de estado
     * @return A raiz da hierarquia de visualização.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdicionarFotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Inicializa o fragmento após a criação da view.
     * Configura RecyclerView, observa LiveData, e trata permissão de câmera.
     *
     * @param view View raiz
     * @param savedInstanceState Bundle de estado
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AdicionarFotoViewModel.class);

        binding.recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FotoAdapter(new ArrayList<>());
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
            iniciarCamera();
        }

        binding.buttonTakePhoto.setOnClickListener(v -> tirarFoto());
    }

    /**
     * Inicializa a câmera e configura a visualização do Preview.
     * Cria uma instância de {@link ImageCapture} para capturar as imagens.
     */
    private void iniciarCamera() {
        imageCapture = new ImageCapture.Builder().build();
        ProcessCameraProvider.getInstance(requireContext()).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get();
                cameraProvider.unbindAll();

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                cameraProvider.bindToLifecycle(this, selector, preview, imageCapture);
            } catch (Exception e) {
                Log.e("Camera", "Erro ao iniciar a câmera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    /**
     * Captura uma foto e solicita ao ViewModel que a salve por meio do Repository.
     * Em caso de sucesso, o caminho da imagem é adicionado ao LiveData para exibição.
     */
    private void tirarFoto() {
        if (imageCapture == null || nomeLocal == null) return;

        Executor executor = ContextCompat.getMainExecutor(requireContext());

        viewModel.salvarFoto(requireContext(), nomeLocal, imageCapture, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                Uri uri = output.getSavedUri() != null
                        ? output.getSavedUri()
                        : Uri.fromFile(new File(output.getSavedUri().getPath()));

                viewModel.adicionarFoto(uri.getPath());
                Toast.makeText(requireContext(), "Foto salva em: " + uri.getPath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("Foto", "Erro ao salvar imagem", exception);
                Toast.makeText(requireContext(), "Erro ao capturar imagem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Intercepta eventos do menu de opções (como clique na seta de navegação na Toolbar).
     * Redireciona para o fragmento home quando a seta de retorno for clicada.
     *
     * @param item Item do menu selecionado
     * @return true se o item foi tratado
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Trata o clique no botão de voltar da toolbar
        if (item.getItemId() == android.R.id.home) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.homeFragment); // Retorna para a Home
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Libera o binding ao destruir a view do fragmento para evitar vazamento de memória.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



