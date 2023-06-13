package com.example.influencer.UI.CheckpointThemeChoose;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.influencer.R;
import com.example.influencer.databinding.FragmentCheckpointThemeChooseBinding;

public class CheckpointThemeChoose_Fragment extends Fragment {

    private FragmentCheckpointThemeChooseBinding binding;
    private CheckpointThemeChooseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckpointThemeChooseBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckpointThemeChooseViewModel.class);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.checkpointThemeRV.setLayoutManager(layoutManager);

        viewModel.getUserCheckpointsThemes().observe(getViewLifecycleOwner(),rowItems -> {
            CheckpointThemeChooseAdapter adapter = new CheckpointThemeChooseAdapter(rowItems);
            binding.checkpointThemeRV.setAdapter(adapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}