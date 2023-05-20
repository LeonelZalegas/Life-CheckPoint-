package com.example.influencer.UI.CheckpointThemeChoose;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.influencer.R;

public class CheckpointThemeChoose_Fragment extends Fragment {

    private CheckpointThemeChooseViewModel mViewModel;

    public static CheckpointThemeChoose_Fragment newInstance() {
        return new CheckpointThemeChoose_Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkpoint_theme_choose_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CheckpointThemeChooseViewModel.class);
        // TODO: Use the ViewModel
    }

}