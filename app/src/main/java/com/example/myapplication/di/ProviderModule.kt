package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.example.myapplication.network.clients.services.EtherScanServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    private const val TIMEOUT = 40L

    @Provides
    @Singleton
    fun provideServicesApi(retrofit: Retrofit): EtherScanServiceApi {
        return EtherScanServiceApi(retrofit)
    }

    @Provides
    @Singleton
    internal fun getGson(gsonBuilder: GsonBuilder): Gson {
        return gsonBuilder.setLenient().create()
    }

    @Provides
    @Singleton
    internal fun getGsonBuilder(): GsonBuilder {
        return GsonBuilder()
    }

    @Provides
    @Singleton
    fun getRetrofit(gson: Gson): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(interceptor)
        val okHttpClient = okHttpClientBuilder.build()
        val builder =  Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient)
        builder.addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(CoroutineCallAdapterFactory())
        return builder.build()
    }
}