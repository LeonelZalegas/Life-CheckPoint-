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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private var cardDataList = mutableListOf<CardData>()
    private var currentCardIndex = 0
    var isInitialDataFetched = false
    private val preFetchThreshold = 2  // Number of cards ahead to pre-fetch

    init {
        fetchInitialCardData()
    }

//    private fun fetchInitialCardData() {
//        viewModelScope.launch {
//            val fetchCards = List(10) { // Create a list of Deferred objects
//                async {
//                    fetchRandomCardData()
//                }
//            }
//            fetchCards.awaitAll()
//            isInitialDataFetched = true
//            if (cardDataList.isNotEmpty()){
//            _cards.value = listOf(cardDataList[currentCardIndex])}
//            else{
//                Log.e("pedruno","la lista esta vacia")
//            }
//        }
//    }

    private fun fetchInitialCardData() {
        viewModelScope.launch {
            val fetchCards = List(10) { // Create a list of Deferred objects
                async {
                    getRandomCardDataUseCase().getOrNull() // Directly process results here
                }
            }
            val results = fetchCards.awaitAll().filterNotNull() // Filter out any potential nulls
            if (results.isNotEmpty()) {
                cardDataList.addAll(results) // Assuming getContentIfNotHandled & peekContent should handle null cases
                _cards.value = listOf(cardDataList[currentCardIndex])
            } else {
                Log.e("pedruno", "la lista esta vacia")
            }
            isInitialDataFetched = true
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