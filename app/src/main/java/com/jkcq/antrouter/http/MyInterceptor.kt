package com.jkcq.antrouter.http

import android.text.TextUtils
import com.jkcq.antrouter.utils.Logger
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

class MyInterceptor constructor(private val showResponse: Boolean = true) :
    Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        requestLog(request)
        val response = chain.proceed(request)
        return responseLog(response)
    }

    private fun responseLog(response: Response): Response {
        try {
            //===>response Logger
            Logger.e(TAG, "========response'Logger=======")
            val builder = response.newBuilder()
            val clone = builder.build()
     //       Logger.e(TAG, "url : " + clone.request.url)
            Logger.e(TAG, "code : " + clone.code)
        //    Logger.e(TAG, "protocol : " + clone.protocol)
            if (!TextUtils.isEmpty(clone.message)) {
                Logger.e(TAG, "message : " + clone.message)
            }


            if (showResponse) {
                var body = clone.body
                if (body != null) {
                    val mediaType = body.contentType()
                    if (mediaType != null) {
                        Logger.e(TAG, "responseBody's contentType : $mediaType")
                        if (isText(mediaType)) {
                            val resp = body.string()
                            Logger.e(TAG, "responseBody's content : $resp")

                            body = resp.toResponseBody(mediaType)
                            return response.newBuilder().body(body).build()
                        } else {
                            Logger.e(
                                TAG,
                                "responseBody's content : " + " maybe [file part] , too large too print , ignored!"
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            Logger.e(TAG, "========response'Logger=======end")
        }

        return response
    }

    private fun requestLog(request: Request) {
        try {
            val url = request.url.toString()
            val headers = request.headers

            Logger.e(TAG, "========request'Logger=======")
            Logger.e(TAG, "method : " + request.method)
            Logger.e(TAG, "url : $url")
            if (headers.size > 0) {
                Logger.e(TAG, "headers : $headers")
            }
            val requestBody = request.body
            if (requestBody != null) {
                val mediaType = requestBody.contentType()
                if (mediaType != null) {
                    Logger.e(TAG, "requestBody's contentType : $mediaType")
                    if (isText(mediaType)) {
                        Logger.e(TAG, "requestBody's content : " + bodyToString(request))
                    } else {
                        Logger.e(
                            TAG,
                            "requestBody's content : " + " maybe [file part] , too large too print , ignored!"
                        )
                    }
                }
            }
            Logger.e(TAG, "========request'Logger=======end")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isText(mediaType: MediaType): Boolean {
        if (mediaType.type == "text") {
            return true
        }
        if (mediaType.subtype == "json" ||
            mediaType.subtype == "xml" ||
            mediaType.subtype == "html" ||
            mediaType.subtype == "webviewhtml"
        )
            return true
        return false
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "something error when show requestBody."
        }

    }

    companion object {
        const val TAG = "OKHTTP"
    }
}