package com.jkcq.antrouter.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.jkcq.antrouter.AntRouterApplication
import com.jkcq.antrouter.R
import com.jkcq.antrouter.adapter.SpinnerClubListAdapter
import com.jkcq.antrouter.bean.ClubBean
import com.jkcq.antrouter.bean.ClubInfo
import com.jkcq.antrouter.http.NetRepository
import com.jkcq.antrouter.mvp.view.MainActivityView
import com.jkcq.antrouter.utils.DeviceUtil
import com.jkcq.antrouter.utils.PermissionUtil
import com.jkcq.antrouter.utils.SavePreferencesData
import com.jkcq.antrouter.utils.ToastUtils
import com.jkcq.appupdate.ApkDownLoadManager
import kotlinx.android.synthetic.main.activity_reg.*
import java.util.*

class RegActivity : Activity(), MainActivityView {
    val mNetRepository: NetRepository by lazy { NetRepository() }
    private var savePreferencesData: SavePreferencesData? = null

    private var adapter: SpinnerClubListAdapter? = null
    private var clubBean: ClubBean? = null
    private var mCurrentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        savePreferencesData = SavePreferencesData(this@RegActivity)

        getclubInfo()

        btn_clear_cache.setOnClickListener {
            savePreferencesData?.clear()
            Toast.makeText(this@RegActivity, "清除缓存成功", Toast.LENGTH_LONG).show()

        }
        btn_unregister.setOnClickListener {
            unRegist(et_pwd.text.toString())
        }
        tv_version_info.text = "当前版本为：${getAppVersionName(packageName)}"
        btn_check_update.setOnClickListener {
            checkPermisson()
        }
        btn_back.setOnClickListener { jumpToMain() }
    }


    private fun getclubInfo() {
        val para = HashMap<String, String>()
        para["mac"] = DeviceUtil.getMac(AntRouterApplication.getApp())
        para["type"] = "1"
        mNetRepository.executeRequest({
            mNetRepository.getClubInfoByMac(para)
        }, {
            /*  savePreferencesData?.putStringData(SavePreferencesData.SP_KEY_MQTT_CLUBINFO, it.clubId)
              savePreferencesData?.putStringData(SavePreferencesData.SP_KEY_CLUBNAME, it.clubName)

              val intent = Intent(this, MainActivity::class.java)
              startActivity(intent)
              finish()*/
        }, {

        })

    }


    private fun unRegist(pwd: String) {
        val para = HashMap<String, String>()
        para["mac"] = DeviceUtil.getMac(AntRouterApplication.getApp())
        para["type"] = "1"
        para["password"] = pwd
        mNetRepository.executeRequest({
            mNetRepository.unRegisterDevice(para)
        }, {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }, {

        })

    }


    override fun getClubSuccess(clubBean: ClubBean?) {
        this.clubBean = clubBean
        if (clubBean != null) {
            adapter = SpinnerClubListAdapter(this@RegActivity, clubBean)
        }
    }

    override fun registerSuccess(flag: Boolean) {
        if (flag) {
            ToastUtils.showToast(this@RegActivity, "注册成功")
        }
    }

    override fun upStatusSuccess(isSuccess: Boolean) {}
    override fun getRegisterClub(info: ClubInfo) {
    }

    override fun onRespondError(message: String) {}

    fun getAppVersionName(packageName: String?): String? {
        return if (TextUtils.isEmpty(packageName)) "" else try {
            val pm: PackageManager = getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }


    fun jumpToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val SP_KEY_MQTT_CLUBINFO = "sp_key_mqtt_clubinfo"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkPermisson() {
        PermissionUtil.checkPermission(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            permissonCallback = object : PermissionUtil.OnPermissonCallback {
                override fun isGrant(grant: Boolean) {
                    if (grant) {
                        checkUpdata()
                    } else {
                        Toast.makeText(this@RegActivity, "没有权限", Toast.LENGTH_SHORT).show()

                    }
                }
            })
    }

    fun checkUpdata() {
        //111  心率墙
        //112  接收器
        mNetRepository.executeRequest({ mNetRepository.checkUpdate("112") }, {
            if (DeviceUtil.getVersionCode(this) < it.appVersionCode && !TextUtils.isEmpty(it.downloadUrl)) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this)
                    .setTitle("发现新版本:" + it.getAppVersionName())
                    .setMessage(it.upgradeDesc)
                    .setPositiveButton(
                        "更新",
                        DialogInterface.OnClickListener { dialog, which ->
                            ApkDownLoadManager(this@RegActivity).startDownLoad(it.downloadUrl)
                        })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .create()
                alertDialog.setCanceledOnTouchOutside(false) //可选

                alertDialog.setCancelable(false) //可选

                alertDialog.show()
            } else {
                ToastUtils.showToast(this@RegActivity, "无更新")
            }
        }, {
            ToastUtils.showToast(this@RegActivity, "无更新")
        })
    }


    fun getDeviceId() {
    }
}