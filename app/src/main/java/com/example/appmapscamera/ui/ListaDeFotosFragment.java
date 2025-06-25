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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.FragmentListaDeFotosBinding;
import com.example.appmapscamera.viewModel.ListaDeFotosViewModel;

public class ListaDeFotosFragment extends Fragment {

    private ListaDeFotosViewModel viewModel;
    private FragmentListaDeFotosBinding binding;
    private FotoAdapter adapter;

    public ListaDeFotosFragment() {
        super(R.layout.fragment_lista_de_fotos);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lista_de_fotos, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recupera argumento (nome do local)
        String localName = ListaDeFotosFragmentArgs.fromBundle(getArguments()).getLocalName();

        viewModel = new ViewModelProvider(this).get(ListaDeFotosViewModel.class);
        viewModel.setLocalName(requireContext(), localName);

        adapter = new FotoAdapter(null);
        binding.recyclerViewFotos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFotos.setAdapter(adapter);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.getFotos().observe(getViewLifecycleOwner(), fotos -> {
            adapter.atualizarFotos(fotos);
        });
    }
}
