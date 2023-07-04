package com.example.influencer.UI.CheckPoint_Tab;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.influencer.UI.CheckpointThemeChoose.CheckpointThemeChooseActivity;
import com.example.influencer.UI.CheckpointUpdateThemeChoose.CheckpointUpdateThemeChooseActivity;
import com.example.influencer.databinding.FragmentCheckpointTabBinding;

public class CheckpointTabFragment extends Fragment {

    private FragmentCheckpointTabBinding binding;
    private CheckpointTabViewModel checkpointTabViewModel;
    Boolean isAllFabsVisible;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentCheckpointTabBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkpointTabViewModel = new CheckpointTabViewModel();

        binding.addingNewCheckpoint.setVisibility(View.GONE);
        binding.addingNewCheckpointUpdate.setVisibility(View.GONE);
        isAllFabsVisible = false;

        initUI();
    }

    private void initUI() {
        initListeners();
        initObservers();
    }

    private void initListeners() {

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabsVisible) {
                    binding.addingNewCheckpoint.show();
                    binding.addingNewCheckpointUpdate.show();
                    isAllFabsVisible = true;
                } else {
                    binding.addingNewCheckpoint.hide();
                    binding.addingNewCheckpointUpdate.hide();
                    isAllFabsVisible = false;
                }
            }
        });

        binding.addingNewCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpointTabViewModel.onAddingNewCheckpointSelected();
            }
        });

        binding.addingNewCheckpointUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpointTabViewModel.onAddingNewCheckpointUpdateSelected();
            }
        });

    }

    private void initObservers() {

        checkpointTabViewModel.navigateToAddingNewCheckpoint.observe(requireActivity(), event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goToAddingNewCheckpoint();
            }
        });

        checkpointTabViewModel.navigateToAddingNewCheckpointUpdate.observe(requireActivity(), event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goToAddingNewCheckpointUpdate();
            }
        });
    }

    private void goToAddingNewCheckpointUpdate() {
        Intent intent_AddingNewCheckpointUpdate= new Intent(getContext(), CheckpointUpdateThemeChooseActivity.class);
        startActivity(intent_AddingNewCheckpointUpdate);
    }

    private void goToAddingNewCheckpoint() {
        Intent intent_AddingNewCheckpoint= new Intent(getContext(), CheckpointThemeChooseActivity.class);
        startActivity(intent_AddingNewCheckpoint);
    }


}
