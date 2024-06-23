package com.example.influencer.Core.Data.Network.NetworkConnectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Boolean>
}