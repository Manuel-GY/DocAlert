package com.docalert.di

import android.content.Context
import com.docalert.data.local.DocAlertDatabase
import com.docalert.data.local.dao.DocumentDao
import com.docalert.data.repository.CalendarRepository
import com.docalert.data.repository.DocumentRepository
import com.docalert.data.repository.OCRRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DocAlertDatabase {
        return DocAlertDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideDocumentDao(database: DocAlertDatabase): DocumentDao {
        return database.documentDao()
    }

    @Provides
    @Singleton
    fun provideDocumentRepository(documentDao: DocumentDao): DocumentRepository {
        return DocumentRepository(documentDao)
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(@ApplicationContext context: Context): CalendarRepository {
        return CalendarRepository(context)
    }

    @Provides
    @Singleton
    fun provideOCRRepository(@ApplicationContext context: Context): OCRRepository {
        return OCRRepository(context)
    }
}
