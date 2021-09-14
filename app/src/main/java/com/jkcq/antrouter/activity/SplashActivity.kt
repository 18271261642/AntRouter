package com.jkcq.antrouter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.jkcq.antrouter.AntRouterApplication
import com.jkcq.antrouter.R
import com.jkcq.antrouter.http.NetRepository
import com.jkcq.antrouter.utils.DeviceUtil
import com.jkcq.antrouter.utils.NetUtils
import com.jkcq.antrouter.utils.SavePreferencesData
import com.jkcq.antrouter.utils.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : Activity() {
    val mNetRepository: NetRepository by lazy { NetRepository() }
    private var savePreferencesData: SavePreferencesData? = null
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //        doZhongHang();
        savePreferencesData = SavePreferencesData(this@SplashActivity)
        requstNetWort()
        //checkRegister()
        btn_register_splash.setOnClickListener {
            regiseterDevice(et_accout.text.toString(), et_pwd.text.toString())
            // startActivity(Intent(this@SplashActivity, RegActivity::class.java))
            // finish()
        }
        btn_test.setOnClickListener {
            checkRegister()
        }
    }


    fun requstNetWort() {
        isConnectIsNomarl()
    }

    /**
     * 判断网络是否连接
     */
    private fun isConnectIsNomarl() {
        if (!NetUtils.hasNetwork(AntRouterApplication.getApp())) {
            handler.postDelayed(Runnable { requstNetWort() }, 3000)
        } else {
            getclubInfo()
            //checkRegister()
        }
    }

    var handler: Handler = Handler()


    private fun regiseterDevice(userName: String, pwd: String) {
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showToast(this, "用户名不能为空")
            return
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(this, "密码不能为空")
            return
        }
        val para = HashMap<String, String>()
        para["password"] = pwd
        para["type"] = "1"
        para["username"] = userName
        para["mac"] = DeviceUtil.getMac(AntRouterApplication.getApp())


        Log.e("mac", "mac=" + DeviceUtil.getMac(AntRouterApplication.getApp()))
        mNetRepository.executeRequest({ mNetRepository.registerDevice(para) }, {
            ToastUtils.showToast(this, "注册成功")
            token = it.token
            getclubInfo()
        }, {
            ToastUtils.showToast(this, it)
        })
    }

    var token: String by Preference(Preference.token, "")

    private fun getclubInfo() {
        val para = HashMap<String, String>()
        para["mac"] = DeviceUtil.getMac(AntRouterApplication.getApp())
        para["type"] = "1"
        mNetRepository.executeRequest({
            mNetRepository.getClubInfoByMac(para)
        }, {
            token = it.token
            savePreferencesData?.putStringData(SavePreferencesData.SP_KEY_MQTT_CLUBINFO, it.clubId)
            savePreferencesData?.putStringData(SavePreferencesData.SP_KEY_CLUBNAME, it.clubName)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, {

        })

    }

    private fun checkRegister() {
        /*val clubId = savePreferencesData?.getStringData(SavePreferencesData.SP_KEY_MQTT_CLUBINFO)
        if (!TextUtils.isEmpty(clubId)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {*/
        /* val map = HashMap<String, String>()
         map["clubId"] = clubId!!
         map["deviceToken"] = DeviceUtil.getMac(AntRouterApplication.getApp())
         mNetRepository.executeRequest({ mNetRepository.getClubInfoByMac(map) }, {
             val intent = Intent(this, MainActivity::class.java)
             startActivity(intent)
             finish()
         }, {
             Toast.makeText(this@SplashActivity, it + "  请求报错或者未注册", Toast.LENGTH_LONG).show()
         })*/
        // }
    }

}