package com.example.influencer.UI.OnBoarding.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.influencer.R;
import com.example.influencer.databinding.FragmentOnboarding2Binding;

public class OnboardingFragment2 extends Fragment {

    private FragmentOnboarding2Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnboarding2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}