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
        ).fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedDatabase(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Optionally re-seed or check if empty on every open during development
                seedDatabase(db)
            }

            private fun seedDatabase(db: SupportSQLiteDatabase) {
                // Clear existing services to ensure new order and images are applied
                db.execSQL("DELETE FROM services")
                
                db.execSQL("INSERT OR REPLACE INTO motorcycles (id, brand, model, year, pricePerDay, availability, imageUrl, description, type) VALUES ('1', 'Ducati', 'Panigale V4', 2024, 250.0, 1, 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?q=80&w=800&auto=format&fit=crop', 'Pure adrenaline. The Panigale V4 is the ultimate expression of Ducati racing DNA.', 'Sport')")
                db.execSQL("INSERT OR REPLACE INTO motorcycles (id, brand, model, year, pricePerDay, availability, imageUrl, description, type) VALUES ('2', 'Harley-Davidson', 'Fat Boy 114', 2024, 180.0, 1, 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80&w=800&auto=format&fit=crop', 'An icon of the road. Power and presence.', 'Cruiser')")
                db.execSQL("INSERT OR REPLACE INTO motorcycles (id, brand, model, year, pricePerDay, availability, imageUrl, description, type) VALUES ('3', 'BMW', 'R 1250 GS', 2024, 210.0, 1, 'https://images.unsplash.com/photo-1591637333184-19aa84b3e01f?q=80&w=800&auto=format&fit=crop', 'Unstoppable. The King of Adventure.', 'Adventure')")
                
                db.execSQL("INSERT INTO services (id, title, price, description, imageUrl, tags, category) VALUES ('1', 'Tuning Performance', '$150', 'Precision ECU remapping and dyno-optimization for maximum power delivery.', 'tuning', 'Performance,Tuning', 'PERFORMANCE')")
                db.execSQL("INSERT INTO services (id, title, price, description, imageUrl, tags, category) VALUES ('2', 'Maintenance', '$80', 'Comprehensive 50-point technical inspection and fluid rejuvenation.', 'maintenance', 'Maintenance,Service', 'MAINTENANCE')")
                db.execSQL("INSERT INTO services (id, title, price, description, imageUrl, tags, category) VALUES ('3', 'Engine Check Up', '$120', 'Advanced sensor & loom verification using professional diagnostic tools.', 'engine_checkup', 'Diagnostics,Engine', 'PERFORMANCE')")
                db.execSQL("INSERT INTO services (id, title, price, description, imageUrl, tags, category) VALUES ('4', 'Washing', '$45', 'Professional deep cleaning and detailing for a showroom finish.', 'washing', 'Cleaning,Detailing', 'MAINTENANCE')")

                db.execSQL("INSERT OR IGNORE INTO parts (name, sku, stockQuantity, price, category) VALUES ('Brembo Brake Pads', 'BRK-001', 50, 85.0, 'BRAKES')")
                db.execSQL("INSERT OR IGNORE INTO parts (name, sku, stockQuantity, price, category) VALUES ('Öhlins Rear Shock', 'SUS-402', 12, 1200.0, 'SUSPENSION')")
                db.execSQL("INSERT OR IGNORE INTO parts (name, sku, stockQuantity, price, category) VALUES ('Akrapovic Exhaust', 'EXH-99', 5, 2400.0, 'PERFORMANCE')")
            }
        }).build()
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
