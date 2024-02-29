package com.example.influencer.UI.OnBoarding___CountryAndDateSelector.CountryAndDateSelector

import androidx.lifecycle.*
import com.example.influencer.Domain.CalculateAgeAndSaveUseCase
import com.example.influencer.Domain.SaveCountryInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryAndDateSelectorViewmodel @Inject constructor(
    private val calculateAgeAndSaveUseCase: CalculateAgeAndSaveUseCase,
    private val saveCountryInfoUseCase: SaveCountryInfoUseCase
) : ViewModel() {

    private val _ageMonthsSaveSuccessLD = MutableLiveData<Boolean>()
    val ageMonthsSaveSuccessLD: LiveData<Boolean> = _ageMonthsSaveSuccessLD

    private val _countryInfosSaveSuccessLD = MutableLiveData<Boolean>()
    val countryInfosSaveSuccessLD: LiveData<Boolean> = _countryInfosSaveSuccessLD

    private val _navigateToHome = MediatorLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun calculateAgeAndSave(selectedDateMillis: Long) {
        viewModelScope.launch {
            try {
                calculateAgeAndSaveUseCase(selectedDateMillis)
                _ageMonthsSaveSuccessLD.postValue(true)
            } catch (e: Exception) {
                _ageMonthsSaveSuccessLD.postValue(false)
            }
        }
    }

    fun storeCountryInfo(countryName: String, countryFlag: Int) {
        viewModelScope.launch {
            try {
                saveCountryInfoUseCase(countryName,countryFlag)
                _countryInfosSaveSuccessLD.postValue(true)
            }catch (e: Exception){
                _countryInfosSaveSuccessLD.postValue(false)
            }
        }
    }

//https://www.notion.so/CountryDate-Activity-43fdd513da03426daeaa723c66608fa1?pvs=4

    init {
        _navigateToHome.addSource(ageMonthsSaveSuccessLD) { ageSuccess ->
            val countrySuccess = countryInfosSaveSuccessLD.value ?: false
            _navigateToHome.value = ageSuccess && countrySuccess
        }

        _navigateToHome.addSource(countryInfosSaveSuccessLD) { countrySuccess ->
            val ageSuccess = ageMonthsSaveSuccessLD.value ?: false
            _navigateToHome.value = ageSuccess && countrySuccess
        }
    }

}