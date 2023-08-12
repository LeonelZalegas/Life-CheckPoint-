package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.CheckpointThemeChoose;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.CheckpointThemeChoose_Fragment;
import com.example.influencer.databinding.ActivityCheckpointThemeChooseBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CheckpointThemeChooseActivity extends AppCompatActivity {

    private ActivityCheckpointThemeChooseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckpointThemeChooseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle args = new Bundle();
        args.putBoolean("showAddNewRow", true);
        args.putString("MainTitle", getString(R.string.Main_title_adding_new_checkpoint));
        args.putString("SecondaryTitle", getString(R.string.Secondary_title_adding_new_checkpoint));

        CheckpointThemeChoose_Fragment checkpointThemeChoose_fragment = new CheckpointThemeChoose_Fragment();
        checkpointThemeChoose_fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(binding.checkpointchoosetheme.getId(), checkpointThemeChoose_fragment)
                .commit();
    }
}