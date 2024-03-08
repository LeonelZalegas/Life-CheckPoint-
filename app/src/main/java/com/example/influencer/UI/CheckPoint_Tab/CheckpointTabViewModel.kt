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

    val loading = MutableLiveData<Boolean>()

    private var cardDataList = mutableListOf<CardData>()
    private var currentCardIndex = 0
    private val preFetchThreshold = 2  // Number of cards ahead to pre-fetch

    init {
        fetchInitialCardData()
    }

//https://www.notion.so/fragment-mostrando-todos-los-post-de-los-usuarios-5c2cf5fac4944b24a7b77b16f5c4472e?pvs=4#21167cd1d176418290427e203e99bf4e
    private fun fetchInitialCardData() {
        viewModelScope.launch {
            loading.postValue(true)
            val fetchCards = List(10) { // Create a list of Deferred objects
                async {
                    getRandomCardDataUseCase().getOrNull() // Directly process results here
                }
            }
            val results = fetchCards.awaitAll().filterNotNull()
            if (results.isNotEmpty()) {
                cardDataList.addAll(results)
                updateCardsForDisplay()
            }
            loading.postValue(false)
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

    fun swipeNextCard() {
        if ((currentCardIndex < cardDataList.size - 1)) {
            currentCardIndex++
            updateCardsForDisplay()
            if (currentCardIndex >= cardDataList.size - preFetchThreshold) {
                preFetchCards()
            }
        } else {
            fetchRandomCardData()
        }
    }

    fun Rewind() {
        currentCardIndex--
        updateCardsForDisplay()
    }

    fun updateCardsForDisplay() {
        val cardsToShow = mutableListOf<CardData>()
        cardsToShow.add(cardDataList[currentCardIndex])
        if ((currentCardIndex + 1) < cardDataList.size) {
            cardsToShow.add(cardDataList[currentCardIndex + 1])
        }
        _cards.value = cardsToShow
    }


    fun isLastCard(): Boolean = currentCardIndex == 0

    fun onAddingNewCheckpointSelected() {
        _navigateToAddingNewCheckpoint.value = Event(true)
    }

    fun onAddingNewCheckpointUpdateSelected() {
        _navigateToAddingNewCheckpointUpdate.value = Event(true)
    }
}