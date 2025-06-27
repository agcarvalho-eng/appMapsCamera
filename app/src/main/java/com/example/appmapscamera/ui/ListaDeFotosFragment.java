package com.example.appmapscamera.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentListaDeFotosBinding;
import com.example.appmapscamera.viewModel.ListaDeFotosViewModel;

/**
 * Fragmento responsável por exibir a lista de fotos de um determinado local.
 */
public class ListaDeFotosFragment extends Fragment {

    private ListaDeFotosViewModel viewModel;
    private FragmentListaDeFotosBinding binding;
    private FotoAdapter adapter;

    /**
     * Construtor padrão do fragmento.
     */
    public ListaDeFotosFragment() {
        super(R.layout.fragment_lista_de_fotos);
    }

    /**
     * Infla o layout do fragmento utilizando DataBinding.
     * @param inflater  O LayoutInflater usado para inflar o layout.
     * @param container O ViewGroup pai.
     * @param savedInstanceState Bundle com estado salvo (opcional).
     * @return A raiz da view inflada.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lista_de_fotos, container, false);
        return binding.getRoot();
    }

    /**
     * Executado após a view ser criada. Inicializa o ViewModel, configura o RecyclerView
     * e atualiza os textos da UI com base no nome do local.
     * @param view               A view retornada por onCreateView().
     * @param savedInstanceState Bundle com estado salvo (opcional).
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recupera o argumento (nome do local) passado por Safe Args
        String nomeLocal = ListaDeFotosFragmentArgs.fromBundle(getArguments()).getNomeLocal();

        // Inicializa o ViewModel e define o nome do local
        viewModel = new ViewModelProvider(this).get(ListaDeFotosViewModel.class);
        viewModel.setNomeLocal(requireContext(), nomeLocal);

        // Configura o RecyclerView com um layout vertical e o adapter
        adapter = new FotoAdapter(null);
        binding.recyclerViewFotos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFotos.setAdapter(adapter);

        // Define o lifecycle owner para permitir atualização automática via LiveData
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Observa a lista de fotos e atualiza o adapter quando houver mudanças
        viewModel.getFotos().observe(getViewLifecycleOwner(), fotos -> {
            adapter.atualizarFotos(fotos);
        });

        // Atualiza o texto do local exibido abaixo do título
        TextView textLocal = view.findViewById(R.id.textLocal);
        textLocal.setText("Local: " + nomeLocal);
    }
}

