package br.com.lucolimac.shesafe.android.framework.constants

import android.util.Log
import br.com.lucolimac.shesafe.android.framework.constants.Endpoints.SMS_DEV_HOST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SmsDevApi {
    private const val OK_HTTP_TAG_LOG = "SMS-DEV-LOG"


    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor { Log.i(OK_HTTP_TAG_LOG, it) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(SMS_DEV_HOST).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}