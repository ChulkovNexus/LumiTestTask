package com.example.myapplication.network;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;

public class ServiceGenerator {

    public static <S> S createService(@NonNull Class<S> serviceClass, @NonNull Retrofit retrofit) {
        return retrofit.create(serviceClass);
    }
}