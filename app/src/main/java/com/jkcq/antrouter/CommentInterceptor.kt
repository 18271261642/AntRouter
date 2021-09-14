package com.jkcq.antrouter

import android.text.TextUtils
import com.jkcq.antrouter.activity.Preference
import com.jkcq.antrouter.utils.Logger
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @Author  Snail
 * @Date 2019-10-11
 * @Description
 **/
class CommentInterceptor : Interceptor {
    private var contentType = "application/json"
    var token: String by Preference(Preference.token, "")
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val builder = original.newBuilder()
        builder.addHeader("Content-Type", contentType)
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("wall-token", "Bearer " + token)
        }

        Logger.d("wall-token", "Bearer " + token)
        val build = builder.build()
        return chain.proceed(build)
    }
}