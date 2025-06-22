package com.example.appmapscamera.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentHomeBinding;
import com.example.appmapscamera.viewModel.HomeViewModel;

/**
 * Fragmento responsável por exibir uma lista de locais cadastrados.
 * Caso não haja locais, exibe uma mensagem informando a ausência.
 */
public class HomeFragment extends Fragment {

    /**
     * Objeto de binding gerado a partir do layout fragment_home.xml.
     * Utilizado para acessar os elementos da UI de forma segura e tipada.
     */
    private FragmentHomeBinding binding;

    /**
     * ViewModel associado a este fragmento, responsável por fornecer os dados dos locais.
     */
    private HomeViewModel viewModel;

    /**
     * Adaptador responsável por exibir a lista de locais no RecyclerView.
     */
    private LocalAdapter adapter;

    /**
     * Método chamado para inflar o layout do fragmento.
     * @param inflater O objeto LayoutInflater utilizado para inflar o layout.
     * @param container O ViewGroup pai no qual o layout será inflado.
     * @param savedInstanceState Estado previamente salvo, se houver.
     * @return A raiz da hierarquia de visualização inflada.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout usando DataBinding e retorna a view raiz
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado logo após a view do fragmento ser criada.
     * Aqui são inicializados o ViewModel, o RecyclerView e os observers.
     * @param view A View retornada por onCreateView.
     * @param savedInstanceState Estado salvo do fragmento, se houver.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Inicializa o ViewModel usando o ViewModelProvider
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Define o layout linear (vertical) para o RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        /**
         * Observa o LiveData que contém a lista de locais.
         * Quando os dados mudam, a UI é atualizada para refletir o novo estado.
         */
        viewModel.getLocais().observe(getViewLifecycleOwner(), locais -> {
            // Se a lista estiver vazia ou nula, mostra mensagem de "vazio" e oculta o RecyclerView
            if (locais == null || locais.isEmpty()) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                // Caso existam locais, mostra o RecyclerView e oculta a mensagem
                binding.textEmpty.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

                /**
                 * Cria e configura o adaptador com a lista de locais e um listener de clique.
                 * Ao clicar em um local, navega para o fragmento de adicionar foto.
                 */
                adapter = new LocalAdapter(locais, local -> {
                    // Prepara um bundle com o nome do local selecionado
                    Bundle bundle = new Bundle();
                    bundle.putString("localName", local.getNome());

                    // Navega para o fragmento de adicionar foto, passando os dados do local
                    Navigation.findNavController(view).navigate(R.id.adicionarFotoFragment, bundle);
                });

                // Define o adaptador no RecyclerView
                binding.recyclerView.setAdapter(adapter);
            }
        });

        // Solicita ao ViewModel que carregue os locais ao iniciar o fragmento
        viewModel.carregarLocais();
    }
}
