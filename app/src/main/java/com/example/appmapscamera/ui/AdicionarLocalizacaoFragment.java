package com.example.appmapscamera.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentAdicionarLocalizacaoBinding;
import com.example.appmapscamera.viewModel.AdicionarLocalizacaoViewModel;

/**
 * Fragmento responsável por permitir que o usuário cadastre um novo local.
 * Ele solicita permissão de localização, exibe as coordenadas atuais e salva os dados informados.
 * A lógica de negócio e acesso à localização é delegada ao ViewModel.
 */
public class AdicionarLocalizacaoFragment extends Fragment {

    /**
     * Objeto de binding associado ao layout fragment_adicionar_localizacao.xml.
     * Permite acesso direto aos componentes da interface de forma segura e tipada.
     */
    private FragmentAdicionarLocalizacaoBinding binding;

    /**
     * ViewModel que contém toda a lógica de localização e validação.
     */
    private AdicionarLocalizacaoViewModel viewModel;

    /**
     * Launcher utilizado para solicitar permissões de localização em tempo de execução.
     */
    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    viewModel.iniciarLocalizacao(); // Inicia a busca por localização
                } else {
                    Toast.makeText(getContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Método chamado para inflar o layout do fragmento.
     * @param inflater Inflador de layout.
     * @param container Contêiner pai.
     * @param savedInstanceState Estado salvo da instância, se houver.
     * @return A raiz da view do fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout com DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_adicionar_localizacao, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado após a view ser criada.
     * Inicializa o ViewModel, observa os dados e configura os eventos da interface.
     * @param view View raiz.
     * @param savedInstanceState Estado salvo da instância, se houver.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(AdicionarLocalizacaoViewModel.class);

        // Solicita permissão de localização
        solicitarPermissaoLocalizacao();

        // Observa coordenadas e atualiza a UI quando disponíveis
        viewModel.getCoordenadas().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                String texto = "Lat: " + location.getLatitude() + " | Lng: " + location.getLongitude();
                binding.textCoordenadas.setText(texto);
            }
        });

        // Observa mensagens de erro, sucesso ou navegação
        viewModel.getMensagem().observe(getViewLifecycleOwner(), msg -> {
            if (msg.startsWith("salvo:")) {
                // Navega para o próximo fragmento após salvar
                String nomeLocal = msg.replace("salvo:", "");
                Bundle bundle = new Bundle();
                bundle.putString("localName", nomeLocal);
                Navigation.findNavController(view).navigate(R.id.adicionarFotoFragment, bundle);
            } else {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Configura o botão de salvar
        binding.buttonSalvar.setOnClickListener(v -> {
            String nome = binding.textNome.getText().toString().trim();
            String raio = binding.textRaio.getText().toString().trim();
            viewModel.salvarLocal(nome, raio); // Lógica delegada ao ViewModel
        });
    }

    /**
     * Verifica se a permissão de localização foi concedida.
     * Caso contrário, solicita ao usuário.
     */
    private void solicitarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            viewModel.iniciarLocalizacao();
        }
    }

    /**
     * Método chamado quando a view do fragmento é destruída.
     * Interrompe a atualização da localização para evitar vazamentos de memória.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.pararLocalizacao(); // Interrompe as atualizações de localização
    }
}

