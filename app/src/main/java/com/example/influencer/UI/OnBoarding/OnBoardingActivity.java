package com.example.influencer.UI.OnBoarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.influencer.R;
import com.example.influencer.UI.Login.LoginActivity;
import com.example.influencer.UI.OnBoarding.Fragments.OnboardingFragment1;
import com.example.influencer.UI.OnBoarding.Fragments.OnboardingFragment2;
import com.example.influencer.UI.OnBoarding.Fragments.OnboardingFragment3;
import com.example.influencer.databinding.ActivityOnboardingBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class OnBoardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingViewModel viewModel;
    private DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        // Set up ViewPager2 adapter
        OnboardingPagerAdapter pagerAdapter = new OnboardingPagerAdapter(this);
        pagerAdapter.addFragment(new OnboardingFragment1());
        pagerAdapter.addFragment(new OnboardingFragment2());
        pagerAdapter.addFragment(new OnboardingFragment3());
        binding.onboardingViewPager.setAdapter(pagerAdapter);

        dotsIndicator = binding.dotsIndicator;
        dotsIndicator.attachTo(binding.onboardingViewPager);

        binding.buttonStartNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.StartnowSelected();
            }
        });

        viewModel.navigateToHome.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                goTonavigateToHome();
            }
        });
    }

    private void goTonavigateToHome() {
        //hace algo
    }
}