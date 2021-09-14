package com.jkcq.antrouter.http

import com.jkcq.antrouter.bean.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 *  Created by BeyondWorlds
 *  on 2020/7/7
 */
interface ApiService {


    /**
     * 注册设备
     *
     */

    @POST("/api/wall-managers/device-register")
    suspend fun deviceRegister(@Body map: Map<String, String>): BaseResponse<TokenBean>


    /**
     *获得心率墙用户登录信息
     */
    @POST("/api/wall-managers/refresh-login-info")
    suspend fun refreshLoginInfo(@Body map: Map<String, String>): BaseResponse<AloneClubInfoBean>

    @POST("/api/wall-managers/logout")
    suspend fun devcieLogout(@Body map: Map<String, String>): BaseResponse<Boolean>

    /**
     * 获取版本信息
     */

    @GET("/api/heartratewall/device-version/latest-version")
    suspend fun getVersionInfo(@Query("deviceType") deviceTypeId: String): BaseResponse<VersionInfo>


}