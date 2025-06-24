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
    private final ActivityResultLauncher<String> requisicaoPermissaoCamera =
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
        // Inicializa o ViewModel que gerencia os dados da tela (seguindo o ciclo de vida do Fragment)
        viewModel = new ViewModelProvider(this).get(AdicionarFotoViewModel.class);

        // Define o layout manager do RecyclerView para ser uma lista vertical (LinearLayout)
        binding.recyclerViewFotos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria o adaptador da lista de fotos, inicialmente com uma lista vazia
        adapter = new FotoAdapter(new ArrayList<>());

        // Define o adaptador no RecyclerView para exibir as fotos
        binding.recyclerViewFotos.setAdapter(adapter);

        // Verifica se o Fragment recebeu argumentos (ex: o nome do local)
        if (getArguments() != null) {
            // Recupera o nome do local passado por argumento
            nomeLocal = getArguments().getString("localName");

            // Pede ao ViewModel para carregar as fotos associadas a esse local
            viewModel.carregarFotos(requireContext(), nomeLocal);
        }

        // Observa as mudanças na lista de fotos armazenada no ViewModel
        // Quando as fotos forem carregadas ou alteradas, atualiza o adaptador
        viewModel.getFotos().observe(getViewLifecycleOwner(), fotos -> adapter.atualizarFotos(fotos));

        // Verifica se a permissão da câmera foi concedida
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, solicita ao usuário
            requisicaoPermissaoCamera.launch(Manifest.permission.CAMERA);
        } else {
            // Se a permissão já foi concedida, inicia a câmera
            iniciarCamera();
        }

        // Define o comportamento do botão "Tirar Foto"
        // Quando clicado, executa o método tirarFoto()
        binding.buttonTakePhoto.setOnClickListener(v -> tirarFoto());
    }


    /**
     * Inicializa a câmera e configura a visualização do Preview.
     * Cria uma instância de {@link ImageCapture} para capturar as imagens.
     */
    private void iniciarCamera() {
        // Cria uma instância do ImageCapture, que será usada para capturar fotos
        imageCapture = new ImageCapture.Builder().build();

        // Solicita uma instância do ProcessCameraProvider (responsável por gerenciar o uso da câmera)
        ProcessCameraProvider.getInstance(requireContext()).addListener(() -> {
            try {
                // Obtém o cameraProvider assim que estiver disponível
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get();

                // Desvincula qualquer uso anterior da câmera (caso haja)
                cameraProvider.unbindAll();

                // Seleciona a câmera traseira padrão
                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Cria uma instância de Preview (visualização ao vivo da câmera)
                Preview preview = new Preview.Builder().build();

                // Define onde a visualização da câmera será exibida na interface (ex: no PreviewView)
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                // Vincula a câmera ao ciclo de vida do Fragment
                // Permite exibir a visualização (preview) e capturar imagens
                cameraProvider.bindToLifecycle(this, selector, preview, imageCapture);

            } catch (Exception e) {
                // Em caso de erro ao iniciar a câmera, registra uma mensagem no log
                Log.e("Camera", "Erro ao iniciar a câmera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext())); // Executor que garante que o listener rode na thread principal
    }


    /**
     * Captura uma foto e solicita ao ViewModel que a salve por meio do Repository.
     * Em caso de sucesso, o caminho da imagem é adicionado ao LiveData para exibição.
     */
    private void tirarFoto() {
        // Verifica se o objeto imageCapture foi inicializado e se o nome do local está definido
        // Se não estiverem, encerra a execução do método
        if (imageCapture == null || nomeLocal == null) return;

        // Obtém um executor que executa tarefas na thread principal (UI thread)
        Executor executor = ContextCompat.getMainExecutor(requireContext());

        // Chama o método do ViewModel para salvar a foto
        // Passa o contexto, o nome do local, o objeto imageCapture, o executor e o callback para tratar o resultado
        viewModel.salvarFoto(requireContext(), nomeLocal, imageCapture, executor, new ImageCapture.OnImageSavedCallback() {

            // Método chamado quando a imagem for salva com sucesso
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                // Obtém a URI da imagem salva (pode ser nula em alguns casos)
                Uri uri = output.getSavedUri() != null
                        ? output.getSavedUri()
                        : Uri.fromFile(new File(output.getSavedUri().getPath()));

                // Adiciona o caminho da foto à lista de fotos do ViewModel
                viewModel.adicionarFoto(uri.getPath());

                // Exibe uma mensagem de sucesso para o usuário
                Toast.makeText(requireContext(), "Foto salva em: " + uri.getPath(), Toast.LENGTH_SHORT).show();
            }

            // Método chamado em caso de erro ao salvar a imagem
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // Registra o erro no log para debug
                Log.e("Foto", "Erro ao salvar imagem", exception);

                // Exibe uma mensagem de erro para o usuário
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



