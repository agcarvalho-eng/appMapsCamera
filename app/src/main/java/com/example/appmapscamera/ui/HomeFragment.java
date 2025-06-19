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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private LocalAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getLocais().observe(getViewLifecycleOwner(), locais -> {
            if (locais == null || locais.isEmpty()) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.textEmpty.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

                adapter = new LocalAdapter(locais, local -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("localName", local.getNome());
                    Navigation.findNavController(view).navigate(R.id.adicionarFotoFragment, bundle);
                });
                binding.recyclerView.setAdapter(adapter);
            }
        });

        viewModel.carregarLocais(); // carrega locais ao abrir o fragmento
    }
}
