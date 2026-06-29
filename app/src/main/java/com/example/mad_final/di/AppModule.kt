package com.example.mad_final.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mad_final.data.local.ApexDatabase
import com.example.mad_final.data.local.dao.BookingDao
import com.example.mad_final.data.local.dao.MotorcycleDao
import com.example.mad_final.data.local.dao.PartDao
import com.example.mad_final.data.local.dao.ServiceDao
import com.example.mad_final.data.local.datastore.UserPreferences
import com.example.mad_final.data.remote.ApexApi
import com.example.mad_final.data.remote.interceptor.AuthInterceptor
import com.example.mad_final.data.remote.interceptor.MockInterceptor
import com.example.mad_final.data.repository.AuthRepositoryImpl
import com.example.mad_final.data.repository.BookingRepositoryImpl
import com.example.mad_final.data.repository.MotorcycleRepositoryImpl
import com.example.mad_final.data.repository.PartRepositoryImpl
import com.example.mad_final.data.repository.ServiceRepositoryImpl
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import com.example.mad_final.domain.repository.PartRepository
import com.example.mad_final.domain.repository.ServiceRepository
import com.example.mad_final.ui.util.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMockInterceptor(): MockInterceptor {
        return MockInterceptor()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideApexDatabase(app: Application): ApexDatabase {
        return Room.databaseBuilder(
            app,
            ApexDatabase::class.java,
            ApexDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(dropAllTables = true)
        .addCallback(com.example.mad_final.data.local.DatabaseSeeder())
        .build()
    }

    @Provides
    @Singleton
    fun provideMotorcycleDao(db: ApexDatabase): MotorcycleDao = db.motorcycleDao

    @Provides
    @Singleton
    fun provideBookingDao(db: ApexDatabase): BookingDao = db.bookingDao

    @Provides
    @Singleton
    fun provideServiceDao(db: ApexDatabase): ServiceDao = db.serviceDao

    @Provides
    @Singleton
    fun providePartDao(db: ApexDatabase): PartDao = db.partDao

    @Provides
    @Singleton
    fun provideMotorcycleRepository(
        api: ApexApi,
        dao: MotorcycleDao
    ): MotorcycleRepository {
        return MotorcycleRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(
        dao: BookingDao
    ): BookingRepository {
        return BookingRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(
        dao: ServiceDao
    ): ServiceRepository {
        return ServiceRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun providePartRepository(
        dao: PartDao
    ): PartRepository {
        return PartRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCartRepository(): com.example.mad_final.domain.repository.CartRepository {
        return com.example.mad_final.data.repository.CartRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ApexApi,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api, userPreferences)
    }

    @Provides
    @Singleton
    fun provideApexApi(
        authInterceptor: AuthInterceptor,
        mockInterceptor: MockInterceptor
    ): ApexApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .addInterceptor(mockInterceptor) // Simulate backend
            .build()

        return Retrofit.Builder()
            .baseUrl(ApexApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApexApi::class.java)
    }
}
