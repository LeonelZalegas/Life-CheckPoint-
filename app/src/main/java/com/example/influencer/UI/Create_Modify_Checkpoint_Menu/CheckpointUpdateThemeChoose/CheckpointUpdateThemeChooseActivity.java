package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointUpdateThemeChoose;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.CheckpointThemeChoose_Fragment;
import com.example.influencer.databinding.ActivityCheckpointUpdateThemeChooseBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CheckpointUpdateThemeChooseActivity extends AppCompatActivity {

    private ActivityCheckpointUpdateThemeChooseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckpointUpdateThemeChooseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle args = new Bundle();
        args.putBoolean("showAddNewRow", false);
        args.putString("MainTitle",getString(R.string.Main_title_adding_new_checkpoint_update) );
        args.putString("SecondaryTitle", getString(R.string.Secondary_title_adding_new_checkpoint_update));

        CheckpointThemeChoose_Fragment checkpointThemeChoose_fragment = new CheckpointThemeChoose_Fragment();
        checkpointThemeChoose_fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(binding.checkpointupdatechoosetheme.getId(), checkpointThemeChoose_fragment)
                .commit();
    }
}