package com.practice.mapa.di

import android.content.Context
import androidx.room.Room
import com.practice.mapa.data.catalog.CartDao
import com.practice.mapa.data.catalog.MapaDatabase
import com.practice.mapa.data.catalog.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatalogModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MapaDatabase =
        Room.databaseBuilder(context, MapaDatabase::class.java, "mapa_db").build()

    @Provides
    fun provideProductDao(db: MapaDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: MapaDatabase): CartDao = db.cartDao()
}
