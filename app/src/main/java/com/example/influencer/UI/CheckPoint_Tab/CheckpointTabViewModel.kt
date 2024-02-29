package com.example.influencer.UI.CheckPoint_Tab

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Event
import com.example.influencer.Domain.GetRandomCardDataUseCase
import com.example.influencer.UI.CheckPoint_Tab.Model.CardData
import kotlinx.coroutines.launch

@HiltViewModel
class CheckpointTabViewModel @Inject constructor(
    private val getRandomCardDataUseCase: GetRandomCardDataUseCase
) : ViewModel() {

    private val _navigateToAddingNewCheckpoint = MutableLiveData<Event<Boolean>>()
    val navigateToAddingNewCheckpoint: LiveData<Event<Boolean>> = _navigateToAddingNewCheckpoint

    private val _navigateToAddingNewCheckpointUpdate = MutableLiveData<Event<Boolean>>()
    val navigateToAddingNewCheckpointUpdate: LiveData<Event<Boolean>> = _navigateToAddingNewCheckpointUpdate

    private val _cards = MutableLiveData<List<CardData>>()
    val cards: LiveData<List<CardData>> = _cards

    private val cardDataList = mutableListOf<CardData>()
    private var currentCardIndex = 0
    var isInitialDataFetched = false
    private val preFetchThreshold = 2  // Number of cards ahead to pre-fetch

    init {
        fetchInitialCardData()
    }

    private fun fetchInitialCardData() {
        viewModelScope.launch {
            for (i in 1..10) {
                fetchRandomCardData()
            }
            isInitialDataFetched = true
            _cards.value = listOf(cardDataList[currentCardIndex])
        }
    }

    private fun fetchRandomCardData() {
        viewModelScope.launch {
            getRandomCardDataUseCase().onSuccess { cardData ->
                cardDataList.add(cardData)
                if (cardDataList.size > 10) {
                    cardDataList.removeAt(0)
                    if (currentCardIndex > 0) {
                        currentCardIndex--
                    }
                }
            }.onFailure { error ->
                Log.e("UserPostViewModel", "Error fetching card data: $error")
            }
        }
    }

    private fun preFetchCards() {
        viewModelScope.launch {
            for (i in 1..preFetchThreshold) {
                fetchRandomCardData()
            }
        }
    }

    fun swipeLeft() {
        if ((currentCardIndex < cardDataList.size - 1) && isInitialDataFetched) {
            currentCardIndex++
            _cards.value = listOf(cardDataList[currentCardIndex])
            if (currentCardIndex >= cardDataList.size - preFetchThreshold) {
                preFetchCards()
            }
        } else {
            fetchRandomCardData()
        }
    }

    fun swipeRight() {
        if (currentCardIndex > 0) {
            currentCardIndex--
            _cards.value = listOf(cardDataList[currentCardIndex])
        }
    }

    fun onAddingNewCheckpointSelected() {
        _navigateToAddingNewCheckpoint.value = Event(true)
    }

    fun onAddingNewCheckpointUpdateSelected() {
        _navigateToAddingNewCheckpointUpdate.value = Event(true)
    }
}