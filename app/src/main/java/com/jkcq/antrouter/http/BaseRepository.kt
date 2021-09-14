package com.jkcq.antrouter.http

import android.util.Log
import android.widget.Toast
import com.jkcq.antrouter.AntRouterApplication
import com.jkcq.antrouter.bean.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Created by BeyondWorlds
 *  on 2020/7/7
 */
abstract class BaseRepository {

    /**
     * 处理请求结果，过滤错误码
     */
    fun <T> executeRequest(
        block: suspend () -> BaseResponse<T>,
        onSuccess: (T) -> Unit,
        error: (String) -> Unit = {}
    ) {


        if (!NetUtil.isNetworkConnected(AntRouterApplication.getApp())) {
            Toast.makeText(AntRouterApplication.getApp(), "  请连接网络", Toast.LENGTH_LONG).show()
            // Toast.makeText(AntRouterApplication.getApp(), "请连接网络").show()
            //如果需要处理，添加livedata观察
            /* mNetworkLiveData.value = mNetworkLiveData.value?.let { !it }*/
            return
        }


        GlobalScope.launch(Dispatchers.IO) {
            executeResponse(block(), onSuccess, error)
        }
    }

    /**
     *结果过滤
     */
    private fun <T> executeResponse(
        response: BaseResponse<T>,
        success: (T) -> Unit,
        error: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            Log.e("http", "result=" + response.toString())
            if (response.code == 0) {
                if (response.data != null) {
                    Log.e("http", "obj=" + response.data.toString())
                    success(response.data!!)
                } else {
                    error(" 请求成功：null data")
                }
            } else {
                if (response.msg != null) {
                    error(response.msg!!)
                } else {
                    error("error null")
                }
            }
        }
    }
}