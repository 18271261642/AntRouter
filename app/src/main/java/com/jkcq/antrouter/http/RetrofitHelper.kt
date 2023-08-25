package com.jkcq.antrouter.http

import com.beyondworlds.wanandroid.net.BaseRetrofitHelper
import com.jkcq.antrouter.AllocationApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 *created by wq on 2019/4/1
 */
object RetrofitHelper : BaseRetrofitHelper() {
    val TIME_OUT = 10L
    private var retrofit: Retrofit? = null
    private var noAuthretrofit: Retrofit? = null

    val mService: ApiService by lazy { getRetrofit()!!.create(ApiService::class.java) }
    val noAuthservice: ApiService by lazy { getNoAutoRetrofit()!!.create(ApiService::class.java) }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            synchronized(RetrofitHelper::class.java) {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(AllocationApi.BaseUrl)  // baseUrl
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
//                            .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                }
            }
        }
        return retrofit
    }

    private fun getNoAutoRetrofit(): Retrofit? {
        if (noAuthretrofit == null) {
            synchronized(RetrofitHelper::class.java) {
                if (noAuthretrofit == null) {
                    noAuthretrofit = Retrofit.Builder()
                        .baseUrl(AllocationApi.BaseUrl)  // baseUrl
                        .client(okHttpClientNo)
                        .addConverterFactory(GsonConverterFactory.create())
//                            .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                }
            }
        }
        return noAuthretrofit
    }


}