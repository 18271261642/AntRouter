package com.beyondworlds.wanandroid.net

import com.jkcq.antrouter.AllocationApi
import com.jkcq.antrouter.http.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  Created by BeyondWorlds
 *  on 2020/6/9
 */
object RetrofitHelper1 : BaseRetrofitHelper() {

    private var retorfit: Retrofit? = null

    val mService by lazy { getService(ApiService::class.java, getRetrofit()!!) }

    private fun getRetrofit(): Retrofit? {
        if (retorfit == null) {
            synchronized(RetrofitHelper1::class.java) {
                if (retorfit == null) {
                    retorfit = Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl(AllocationApi.BaseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                }
            }
        }
        return retorfit

    }


}