package com.example.influencer.UI.OnBoarding___CountryAndDateSelector.CountryAndDateSelector

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.influencer.R
import com.example.influencer.UI.Home
import com.example.influencer.databinding.ActivityCountryDateBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//TODO tratar de hacer una unica Activity y hacer 2 fragments, uno el OnBoarding y el otro esta
@AndroidEntryPoint
class CountryDateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryDateBinding
    private val viewModel: CountryAndDateSelectorViewmodel by viewModels()
    private var selectedDate: Long? = null
    private var selectedCountry:String? = null
    private var countryFlag: Int = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datePickerListener()
        countrySelectorListener()
        continueButtomListener()
        setupObservers()
    }

    private fun continueButtomListener() {
        binding.btnContinue.setOnClickListener {
            if (selectedDate == null || selectedCountry.isNullOrEmpty()) {
                Toast.makeText(this, R.string.NoDate_Country, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.calculateAgeAndSave(selectedDate!!)
                viewModel.storeCountryInfo(selectedCountry!!,countryFlag)
            }
        }
    }

    private fun datePickerListener() {
        binding.chipDatePicker.setOnClickListener {

            // Restrict dates to before the current date
            val constraintsBuilder = CalendarConstraints.Builder().apply {
                val validators = ArrayList<CalendarConstraints.DateValidator>()
                validators.add(DateValidatorPointBackward.now())
                setValidator(CompositeDateValidator.allOf(validators))
            }

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.DatePicker_dialog)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Store the selected date
                selectedDate = selection

                // Update the Chip text to display the selected date
                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                val dateText = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                binding.chipDatePicker.text = dateText
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun countrySelectorListener() {
        binding.ccp.setOnCountryChangeListener {
            selectedCountry = binding.ccp.selectedCountryName
            countryFlag = binding.ccp.selectedCountryFlagResourceId
        }
    }

    private fun setupObservers() {
        viewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val intentHome = Intent(this@CountryDateActivity, Home::class.java)
                startActivity(intentHome)
                finish()
            }
        }
    }
}