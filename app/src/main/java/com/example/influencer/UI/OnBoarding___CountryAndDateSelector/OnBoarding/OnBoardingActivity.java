package com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.influencer.UI.Home;
import com.example.influencer.UI.OnBoarding___CountryAndDateSelector.CountryAndDateSelector.CountryDateActivity;
import com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding.Fragments.OnboardingFragment1;
import com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding.Fragments.OnboardingFragment2;
import com.example.influencer.UI.OnBoarding___CountryAndDateSelector.OnBoarding.Fragments.OnboardingFragment3;
import com.example.influencer.databinding.ActivityOnboardingBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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

        //para la barrita de los dots del desplazamiento
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
        Intent intent_save_birthdate_country = new Intent(OnBoardingActivity.this, CountryDateActivity.class);
        startActivity(intent_save_birthdate_country);
        finish();
    }
}