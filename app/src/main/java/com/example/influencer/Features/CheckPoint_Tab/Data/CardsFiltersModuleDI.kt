package com.example.influencer.Features.CheckPoint_Tab.Data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CardsFiltersModuleDI {

    @Binds
    @Singleton
    abstract fun bindCardsFilters(
        cardsFiltersRepository: CardsFiltersRepository
    ):CardsFilters
}