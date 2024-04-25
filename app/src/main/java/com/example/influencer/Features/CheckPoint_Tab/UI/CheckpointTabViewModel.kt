package com.example.influencer.Features.CheckPoint_Tab.UI

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.influencer.Core.Utils.Event
import com.example.influencer.LaNuevaEstr.Features.CheckPoint_Tab.Domain.GetPostUpdatesListUseCase
import com.example.influencer.LaNuevaEstr.Features.CheckPoint_Tab.Domain.GetRandomCardDataUseCase
import com.example.influencer.LaNuevaEstr.Features.CheckPoint_Tab.Domain.LikesInteractionsUseCase
import com.example.influencer.LaNuevaEstr.Features.CheckPoint_Tab.Domain.Model.CardData
import kotlinx.coroutines.*
import java.util.*

@HiltViewModel
class CheckpointTabViewModel @Inject constructor(
    private val getRandomCardDataUseCase: GetRandomCardDataUseCase,
    private val likesInteractionsUseCase: LikesInteractionsUseCase,
    private val getPostUpdatesListUseCase: GetPostUpdatesListUseCase
) : ViewModel() {

    companion object {
        private const val INITIAL_CARDS_COUNT = 10
        private const val PREFETCH_THRESHOLD = 3  // Number of cards ahead to pre-fetch// con 2 si el user pasa muy rapido no llega a cargar y tmb aveces no carga el card de atras x alguna razon
    }

    private val _navigateToAddingNewCheckpoint = MutableLiveData<_root_ide_package_.com.example.influencer.Core.Utils.Event<Boolean>>()
    val navigateToAddingNewCheckpoint: LiveData<_root_ide_package_.com.example.influencer.Core.Utils.Event<Boolean>> = _navigateToAddingNewCheckpoint

    private val _navigateToAddingNewCheckpointUpdate = MutableLiveData<_root_ide_package_.com.example.influencer.Core.Utils.Event<Boolean>>()
    val navigateToAddingNewCheckpointUpdate: LiveData<_root_ide_package_.com.example.influencer.Core.Utils.Event<Boolean>> = _navigateToAddingNewCheckpointUpdate

    private val _cards = MutableLiveData<List<CardData>>()
    val cards: LiveData<List<CardData>> = _cards

    private val _likeUpdate = MutableLiveData<Pair<String, Int>>()
    val likeUpdate: LiveData<Pair<String, Int>> = _likeUpdate

    val loading = MutableLiveData<Boolean>()

    private var cardDataList = mutableListOf<CardData>()
    private var currentCardIndex = 0

    init {
        fetchInitialCardData()
    }

//https://www.notion.so/fragment-mostrando-todos-los-post-de-los-usuarios-5c2cf5fac4944b24a7b77b16f5c4472e?pvs=4#21167cd1d176418290427e203e99bf4e
    private fun fetchInitialCardData() {
        viewModelScope.launch {
            loading.postValue(true)
            val fetchCards = List(INITIAL_CARDS_COUNT) { // Create a list of Deferred objects
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
                if (cardDataList.size > INITIAL_CARDS_COUNT) {
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
            for (i in 1..PREFETCH_THRESHOLD) {
                fetchRandomCardData()
            }
        }
    }

    fun swipeNextCard() {
        if ((currentCardIndex < cardDataList.size - 1)) {
            currentCardIndex++
            updateCardsForDisplay()
            if (currentCardIndex >= cardDataList.size - PREFETCH_THRESHOLD) {
                preFetchCards()
            }
        } else {
            fetchRandomCardData()
        }
    }

    fun rewind() {
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

    fun likePost(postId: String, postOwnerId: String) {
        viewModelScope.launch {
            try {
               val currentLikes = likesInteractionsUseCase.likePost(postId,postOwnerId)
                _likeUpdate.value = postId to (currentLikes)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error liking post", e)
            }
        }
    }

    fun unlikePost(postId: String, postOwnerId: String) {
        viewModelScope.launch {
            try {
                val currentLikes = likesInteractionsUseCase.unlikePost(postId,postOwnerId)
                _likeUpdate.value = postId to (currentLikes)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error unliking post", e)
            }
        }
    }

    fun checkIfPostIsLiked(postId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isLiked = likesInteractionsUseCase.isPostLiked(postId)
            callback(isLiked)
        }
    }

    fun onAddingNewCheckpointSelected() {
        _navigateToAddingNewCheckpoint.value =
            _root_ide_package_.com.example.influencer.Core.Utils.Event(true)
    }

    fun onAddingNewCheckpointUpdateSelected() {
        _navigateToAddingNewCheckpointUpdate.value =
            _root_ide_package_.com.example.influencer.Core.Utils.Event(true)
    }

    fun fetchUpdatesForPost(postId: String,postOwnerId: String, callback: (SortedMap<Int, String>?) -> Unit) {
        viewModelScope.launch {
            val updatesMap = getPostUpdatesListUseCase(postId,postOwnerId) // This should be a suspend function call
            callback(updatesMap)
        }
    }
}