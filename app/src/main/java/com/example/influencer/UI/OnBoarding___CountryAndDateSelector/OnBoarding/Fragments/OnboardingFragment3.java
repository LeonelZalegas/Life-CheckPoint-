package com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.influencer.databinding.FragmentOnboarding3Binding;


public class OnboardingFragment3 extends Fragment {

    private FragmentOnboarding3Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnboarding3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}