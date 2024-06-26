package com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.CountryAndDateSelector.CountryDateActivity
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding.Fragments.OnboardingFragment1
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding.Fragments.OnboardingFragment2
import com.example.influencer.Features.OnBoarding___CountryAndDateSelector.UI.OnBoarding.Fragments.OnboardingFragment3
import com.example.influencer.databinding.ActivityOnboardingBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var dotsIndicator: DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupDotsIndicator()
        setupStartNowButton()
        observeNavigationEvents()
    }

    private fun setupViewPager() {
        val pagerAdapter = OnboardingPagerAdapter(this).apply {
            addFragment(OnboardingFragment1())
            addFragment(OnboardingFragment2())
            addFragment(OnboardingFragment3())
        }
        binding.onboardingViewPager.adapter = pagerAdapter
    }

    private fun setupDotsIndicator() {
        dotsIndicator = binding.dotsIndicator
        dotsIndicator.attachTo(binding.onboardingViewPager)
    }

    private fun setupStartNowButton() {
        binding.buttonStartNow.setOnClickListener {
            viewModel.startNowSelected()
        }
    }

    private fun observeNavigationEvents() {
        viewModel.navigateToHome.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, CountryDateActivity::class.java)
        startActivity(intent)
        finish()
    }
}