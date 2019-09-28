package com.icapps.sampleapplication

import android.app.Application

import com.icapps.niddler.core.AndroidNiddler
import com.icapps.niddler.interceptor.okhttp.NiddlerOkHttpInterceptor
import com.icapps.niddler.retrofit.NiddlerRetrofitCallInjector
import com.icapps.sampleapplication.api.ExampleJsonApi
import com.icapps.sampleapplication.api.ExampleXMLApi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by maartenvangiel on 10/11/2016.
 */
class NiddlerSampleApplication : Application() {

    lateinit var jsonPlaceholderApi: ExampleJsonApi
        private set
    lateinit var xmlPlaceholderApi: ExampleXMLApi
        private set

    override fun onCreate() {
        super.onCreate()

        val niddler = AndroidNiddler.Builder()
                .setPort(0)
                .setNiddlerInformation(AndroidNiddler.fromApplication(this))
                .setMaxStackTraceSize(10)
                .build()

        niddler.attachToApplication(this)

        val okHttpInterceptor = NiddlerOkHttpInterceptor(niddler, "Default")
        okHttpInterceptor.blacklist(".*raw\\.githubusercontent\\.com.*")

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(okHttpInterceptor)
                .build()

        val jsonRetrofitBuilder = Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
        NiddlerRetrofitCallInjector.inject(jsonRetrofitBuilder, niddler, okHttpClient)
        val jsonRetrofit = jsonRetrofitBuilder.build()
        jsonPlaceholderApi = jsonRetrofit.create(ExampleJsonApi::class.java)

        val xmlRetrofit = Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/")
                .client(okHttpClient)
                .build()
        xmlPlaceholderApi = xmlRetrofit.create(ExampleXMLApi::class.java)
    }

}