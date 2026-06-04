package com.example.bingelist.di

import android.app.Application
import androidx.room.Room
import com.example.bingelist.data.local.DramaDao
import com.example.bingelist.data.local.DramaDatabase
import com.example.bingelist.data.remote.DramaApi
import com.example.bingelist.data.repository.DramaRepositoryImpl
import com.example.bingelist.domain.repository.DramaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("api_key", "b5e1fe63f1f863d321bb75f60364e26d")
                    .build()
                val request = original.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideDramaApi(client: OkHttpClient): DramaApi {
        return Retrofit.Builder()
            .baseUrl(DramaApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(DramaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDramaDatabase(app: Application): DramaDatabase {
        return Room.databaseBuilder(
            app,
            DramaDatabase::class.java,
            DramaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDramaDao(db: DramaDatabase): DramaDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideDramaRepository(
        api: DramaApi,
        dao: DramaDao,
        db: DramaDatabase
    ): DramaRepository {
        return DramaRepositoryImpl(api, dao, db)
    }
}
