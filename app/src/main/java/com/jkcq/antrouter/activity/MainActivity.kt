package com.jkcq.antrouter.activity

import android.content.*
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.jkcq.antrouter.AntRouterApplication
import com.jkcq.antrouter.R
import com.jkcq.antrouter.adapter.SpinnerClubListAdapter
import com.jkcq.antrouter.bean.*
import com.jkcq.antrouter.http.NetRepository
import com.jkcq.antrouter.listener.AntReceiveListener
import com.jkcq.antrouter.mvp.BaseMVPActivity
import com.jkcq.antrouter.mvp.presenter.MainActivityPresenter
import com.jkcq.antrouter.mvp.view.MainActivityView
import com.jkcq.antrouter.usbserial.TemperatureUsbControl
import com.jkcq.antrouter.usbserial.util.HexDump
import com.jkcq.antrouter.utils.*
import com.jkcq.appupdate.ApkDownLoadManager
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_check_update
import kotlinx.android.synthetic.main.activity_main.btn_unregister
import kotlinx.android.synthetic.main.activity_main.et_pwd
import kotlinx.android.synthetic.main.activity_main.tv_version_info
import kotlinx.android.synthetic.main.activity_reg.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class MainActivity : BaseMVPActivity<MainActivityView?, MainActivityPresenter?>(),
    MainActivityView {


    private val tgs = "MainActivity"


    private val logStr = StringBuffer()

    //当前接收到的数据长度
    private var mCurrentDataLength = 0
    private var adapter: SpinnerClubListAdapter? = null

    //    private ClubBean clubBean;
    private var mCurrentIndex = 0
    private var mTemperatureUsbControl: TemperatureUsbControl? = null
    private var mSecondDisposable: Disposable? = null
    private val mSecondDisposableSn: Disposable? = null
    private val mCurrentMap = ConcurrentHashMap<String?, String>()
    private val mCurrentPublishMap = ConcurrentHashMap<String?, String>()
    private val mCurrentPublishMapSn = ConcurrentHashMap<String, String>()
    private var isUsbNormal = false
    private var isMqttNormal = false
    private var isClubNormal = false


    val mNetRepository: NetRepository by lazy { NetRepository() }


    //private String SERVER_URL = "192.168.10.102:61613";
    // private val SERVER_URL = "120.77.62.190:61613"

    private val SERVER_URL = "mqtt.fitalent.com.cn:61613"
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                HANDLE_WHAT_TYPE -> tv_status_log.text = logStr.toString()
            }
        }
    }

    //俱乐部ID
    private var CLUB_ID = ""
    private val HEART_RATE_SYSTEM = "HEART_RATE_SYSTEM"
    private var savePreferencesData: SavePreferencesData? = null
    private val stringBuffer = StringBuilder() //发布的数据
    var testStr = ""
    var receiverData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requstNetWort()
        setSettingItem()
    }


    fun hasNetNext() {
        //先判断网络通讯是否
        testStr = this.resources.getString(R.string.devicesnumber)
        receiverData = this.resources.getString(R.string.receiver_data_length)
        //初始化Log写入工具
        FileUtil.initFile(this@MainActivity)
        path = FileUtil.getFile()
        current_time = DateUtils.getStrTime(System.currentTimeMillis())
        savePreferencesData = SavePreferencesData(this@MainActivity)


        //俱乐部缓存数据
        val clubId = savePreferencesData?.getStringData(SavePreferencesData.SP_KEY_MQTT_CLUBINFO)
        if (!TextUtils.isEmpty(clubId)) {
            isClubNormal = true
            CLUB_ID = clubId!!
            spinner!!.visibility = View.GONE
            tv_club_name.visibility = View.VISIBLE
            tv_club_name.text =
                savePreferencesData!!.getStringData(SavePreferencesData.SP_KEY_CLUBNAME)
        }
        //        initMqtt();
        spinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                mCurrentIndex = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                mCurrentIndex = 0
            }
        }
        val filterusb = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(mUsbPermissionActionReceiver, filterusb)
        EventBus.getDefault().register(this)
        afterGetUsbPermission(null)
        sendHeartRateBySecond()
        //    tv_save.setOnClickListener { initMqtt() }
    }


    fun setSettingItem() {
        btn_unregister.setOnClickListener {
            unRegist(et_pwd.text.toString())
        }
        tv_version_info.text = "当前版本为：${getAppVersionName(packageName)}"
        btn_check_update.setOnClickListener {
            checkPermisson()
        }
    }

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


    fun checkPermisson() {
        PermissionUtil.checkPermission(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            permissonCallback = object : PermissionUtil.OnPermissonCallback {
                override fun isGrant(grant: Boolean) {
                    if (grant) {
                        checkUpdata()
                    } else {
                        Toast.makeText(this@MainActivity, "没有权限", Toast.LENGTH_SHORT).show()

                    }
                }
            })
    }


    fun checkUpdata() {
        //111  心率墙
        //112  接收器
        mNetRepository.executeRequest({ mNetRepository.checkUpdate("receiver") }, {
            if (DeviceUtil.getVersionCode(this) < it.appVersionCode && !TextUtils.isEmpty(it.downloadUrl)) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this)
                    .setTitle("发现新版本:" + it.getAppVersionName())
                    .setMessage(it.upgradeDesc)
                    .setPositiveButton(
                        "更新",
                        DialogInterface.OnClickListener { dialog, which ->
                            ApkDownLoadManager(this@MainActivity).startDownLoad(it.downloadUrl)
                        })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .create()
                alertDialog.setCanceledOnTouchOutside(false) //可选

                alertDialog.setCancelable(false) //可选

                alertDialog.show()
            } else {
                ToastUtils.showToast(this@MainActivity, "无更新")
            }
        }, {
            ToastUtils.showToast(this@MainActivity, "无更新")
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

    /**
     * 判断网络是否连接
     */
    private fun isConnectIsNomarl(): Boolean {
        val connectivityManager =
            this.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return if (info != null && info.isAvailable) {
            val name = info.typeName
            Log.i("isConnectIsNomarl", "当前网络名称：$name")
            true
        } else {
            Log.i("isConnectIsNomarl", "没有可用网络")
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            handler.postDelayed({ requstNetWort() }, 3000)
            false
        }
    }


    fun requstNetWort() {
        val calendar = Calendar.getInstance()
        //if (calendar[Calendar.YEAR] >= 2019) {
        if (isConnectIsNomarl()) {
            hasNetNext()
            // mActPresenter.getDeviceTypeId();
        } else {
            ToastUtils.showToast(this@MainActivity, "请连接网络")
        }
        /* } else {
             if (isConnectIsNomarl()) {
                 mActPresenter.getSystemTime()
             }
         }*/
    }

    override fun createPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    override fun getClubSuccess(clubBean: ClubBean?) {
//        this.clubBean = clubBean;
        if (clubBean != null) {
            adapter = SpinnerClubListAdapter(this@MainActivity, clubBean)
            spinner!!.adapter = adapter
        }
    }

    override fun registerSuccess(flag: Boolean) {
        if (flag) {
            ToastUtils.showToast(this@MainActivity, "成功")
        }
    }

    override fun getRegisterClub(info: ClubInfo) {
        if (info != null && !TextUtils.isEmpty(info.id)) {
            CLUB_ID = info.id
            isClubNormal = true
            spinner!!.visibility = View.GONE
            tv_club_name.visibility = View.VISIBLE
            tv_club_name.text = info.name
            savePreferencesData!!.putStringData(
                SP_KEY_MQTT_CLUBINFO,
                JsonUtils.getInstance().toJSON(info)
            )
        } else {
            tv_club_name.visibility = View.GONE
            spinner!!.visibility = View.VISIBLE
            isClubNormal = false
            changeStatus("网关设备未注册")
        }
    }

    override fun upStatusSuccess(isSuccess: Boolean) {
        if (isSuccess) {
            ToastUtils.showToast(this@MainActivity, "上报成功")
        }
    }


    private val mUsbPermissionActionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val usbDevice =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice


                    Log.e(tgs,"--------userDevice="+Gson().toJson(usbDevice)+"\n"+usbDevice.deviceName+"\n"+usbDevice.deviceId)


                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            if (usbDevice.productId == 24857 && usbDevice.vendorId == 1003) {
                                afterGetUsbPermission(usbDevice)
                            }
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context, "设备无权限", Toast.LENGTH_LONG).show()
                        changeStatus("USB设备无权限")
                        isUsbNormal = false
                    }
                }
            }
        }
    }

    //取到权限之后，打开接收器服务
    fun afterGetUsbPermission(device: UsbDevice?) {
        initUsbControl()
        val usbFilter = IntentFilter()
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(mUsbReceiver, usbFilter)
        mTemperatureUsbControl!!.onDeviceStateChange()
    }

    /**
     * 初始化USB
     */
    private fun initUsbControl() {
        mTemperatureUsbControl = TemperatureUsbControl(this@MainActivity)
        mTemperatureUsbControl!!.initUsbControl()
        mTemperatureUsbControl!!.setAntReceiveListener(antReceiveListener)
    }

    /**
     * 用于检测usb插入状态的BroadcasReceiver
     */
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                //设备插入
                mTemperatureUsbControl!!.initUsbControl()
                mTemperatureUsbControl!!.onDeviceStateChange()
                Log.e(Companion.TAG, "ACTION_USB_DEVICE_ATTACHED")
                changeStatus("USB设备已连接")
                isUsbNormal = true
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                //设备移除
                isUsbNormal = false
                mTemperatureUsbControl!!.onPause()
                Log.e(Companion.TAG, "ACTION_USB_DEVICE_DETACHED")
                changeStatus("USB设备被移除")
            }
        }
    }
    private val antReceiveListener: AntReceiveListener = object : AntReceiveListener {
        override fun onNewData(data: ByteArray) {
            Log.e(Companion.TAG, "onNewData")
            isUsbNormal = true
            updateReceivedData(data)
        }

        override fun onRunError(e: Exception) {
            isUsbNormal = false
            changeStatus("Ant数据接收异常： " + e.message)
            Log.e("shao", "--------RunError:" + e.message)
        }
    }

    //***********************************************Mqtt部分****************************************************//
    var mqttAndroidClient: MqttAndroidClient? = null
    var mqttConnectOptions: MqttConnectOptions? = null

    //final String serverUri = "tcp://192.168.10.102:61613";
    var serverUri = ""
    var clientId = "ExampleAndroidClient1112"
    private val userName = "admin"
    private val passWord = "fitalent@1."
    private fun initMqtt() {
        serverUri = "tcp://$SERVER_URL"
        clientId = clientId + System.currentTimeMillis()
        //创建客户端
        mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)
        mqttAndroidClient!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                isMqttNormal = if (reconnect) {
                    Log.e(Companion.TAG, "Reconnected to : $serverURI")
                    // Because Clean Session is true, we need to re-subscribe
                    // subscribeToTopic();//不用订阅，只用发布
                    changeStatus("重新连接MQTT服务器 :$serverURI")
                    false
                } else {
                    Log.e(Companion.TAG, "Connected to: $serverURI")
                    changeStatus("连接MQTT服务器成功,地址为：$serverURI")
                    true
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e(Companion.TAG, "The Connection was lost.")
                if (cause != null) {
                    changeStatus("连接MQTT服务器失败 :" + cause.message)
                }
                isMqttNormal = false
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.e("Incoming message: ", String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Log.d(TAG, "deliveryComplete");
            }
        })

        //创建连接
        mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions!!.isAutomaticReconnect = true
        mqttConnectOptions!!.isCleanSession = false
        mqttConnectOptions!!.userName = userName
        mqttConnectOptions!!.password = passWord.toCharArray()
        mqttConnectOptions!!.maxInflight = 100
        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient!!.connect(mqttConnectOptions, null, IMqttActionListener)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }/*没有可用网络的时候，延迟3秒再尝试重连*/

    /**
     * 判断网络是否连接
     */
    private val isConnectIsNomarls: Boolean
        private get() {
            val connectivityManager =
                this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivityManager.activeNetworkInfo
            return if (info != null && info.isAvailable) {
                val name = info.typeName
                Log.i(Companion.TAG, "当前网络名称：$name")
                true
            } else {
                Log.i(Companion.TAG, "没有可用网络")
                /*没有可用网络的时候，延迟3秒再尝试重连*/Handler().postDelayed({ reConnect() }, 3000)
                false
            }
        }

    private val IMqttActionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken) {
            try {
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                if (mqttAndroidClient != null) {
                    mqttAndroidClient!!.setBufferOpts(disconnectedBufferOptions)
                }
                //                    subscribeToTopic();//不用订阅，只用发布
                changeStatus("连接成功，可以开始发布数据")
                isMqttNormal = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.e(Companion.TAG, "Failed to connect to: $serverUri")
            changeStatus("连接Mqtt失败,重连中...")
            isMqttNormal = false
            reConnect()
        }
    }

    fun reConnect() {
        try {
            if (!isConnected && isConnectIsNomarls) {
                mqttAndroidClient!!.connect(mqttConnectOptions, null, IMqttActionListener)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val isConnected: Boolean
        private get() = if (mqttAndroidClient == null) false else mqttAndroidClient!!.isConnected

    private fun disconnect() {
        if (mqttAndroidClient != null) {
            try {
                mqttAndroidClient?.disconnect()
            } catch (e: Exception) {
                Log.e(Companion.TAG, e.toString())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(mqttMessage: MQTTMessage?) {
//        publishMessage(mqttMessage.getMessage());
    }

    var message = MqttMessage()
    fun publishMessage(mPublishTopic: String?, publishMessage: String) {
        try {
//            LogUtil.e(TAG, "mPublishTopic" + mPublishTopic + "pulishMessage: " + publishMessage);
            if (mqttAndroidClient != null && mqttAndroidClient!!.isConnected) {
                if (!TextUtils.isEmpty(publishMessage)) {
                    message.payload = publishMessage.toByteArray()
                    Log.e(
                        "shao",
                        "mqttAndroidClient.isConnected(): " + mqttAndroidClient!!.isConnected
                    )
                    mqttAndroidClient!!.publish(mPublishTopic, message)
                }
            }
        } catch (e: Exception) {
            System.err.println("Error Publishing: " + e.message)
            changeStatus("发布失败，" + e.message)
            isMqttNormal = false
            e.printStackTrace()
        }
    }

    //==============MQTT=============================
    private val mCountCurrentMap = ConcurrentHashMap<String, HeartBean>()
    private var mTotalBytes = 0 //接收到的总字节数
    private val mCurrentBytes = 0 //当前秒数到达的字节数
    private var path = ""
    private var current_time = ""

    //每一分钟
    private val mTotalMap = ConcurrentHashMap<String, FirstRecevicerBean>()
    private val mAllreadyPaserPackage = 0 // 累计解析包数
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                HANDLER_MESSAGES_CODE -> {
                    val testStr = resources.getString(R.string.devicesnumber)
                    val result = String.format(testStr, mCurrentPublishMap.size)
                    tv_devices_number.text = result
                    val receiverData = resources.getString(R.string.receiver_data_length)
                    val data = String.format(receiverData, mCurrentDataLength)
                    tv_data_total_length.text = data
                    mCurrentDataLength = 0

                    //0 正常，1 异常
                    if (isMqttNormal && isUsbNormal && isClubNormal) {
                        iv_status_img.setImageResource(R.drawable.normal)
                        tv_status_tips.text = "正常"
                        //mActPresenter.uploadStatus(0, "正常");
                    } else {
                        iv_status_img.setImageResource(R.drawable.error)
                        if (!isUsbNormal && !isMqttNormal && !isClubNormal) {
                            tv_status_tips.text = "异常 : 请确保 1、Ant设备已注册，2、服务器启动且地址正确，3、接收器连接正常"
                        } else {
                            if (!isMqttNormal && !isUsbNormal) {
                                tv_status_tips.text = "异常 : MQTT未连接和USB未连接"
                                //mActPresenter.uploadStatus(1, "异常 : MQTT未连接和USB未连接");
                            } else if (!isMqttNormal && !isClubNormal) {
                                tv_status_tips.text = "异常 : MQTT未连接和设备未注册"
                                //mActPresenter.uploadStatus(1, "异常 : MQTT未连接和设备未注册");
                            } else if (!isUsbNormal && !isClubNormal) {
                                tv_status_tips.text = "异常 : USB未连接和设备未注册"
                                // mActPresenter.uploadStatus(1, "异常 : USB未连接和设备未注册");
                            } else if (!isUsbNormal) {
                                //mActPresenter.uploadStatus(1, "异常 : USB未连接");
                                tv_status_tips.text = "异常 : USB未连接"
                            } else if (!isMqttNormal) {
                                // mActPresenter.uploadStatus(1, "异常 : MQTT连接异常");
                                tv_status_tips.text = "异常 : MQTT连接 $SERVER_URL 异常"
                                initMqtt()
                            } else if (isClubNormal) {
                                // mActPresenter.uploadStatus(1, "异常 : 设备未注册");
                                tv_status_tips.text = "异常 : 设备未注册"
                            }
                        }
                    }
                }
            }
        }
    }
    var oneKey: String? = null
    var oneValue: String? = null
    var allKey: String? = null
    var allValue: String? = null

    // 一秒一次，数据分发到MQTT 和Coap
    private val mTimes = 0
    fun sendHeartRateBySecond() {
        //一秒发送一次数据刷新
        if (mSecondDisposable != null && !mSecondDisposable!!.isDisposed) {
            mSecondDisposable!!.dispose()
        }
        if (mSecondDisposableSn != null && !mSecondDisposableSn.isDisposed) {
            mSecondDisposableSn.dispose()
        }
        mSecondDisposable = Observable.interval(1, 2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation()) //.observeOn(AndroidSchedulers.mainThread()) // 由于interval默认在新线程，所以我们应该切回主线程
            .subscribe {
                mCurrentPublishMap.clear()
                mCurrentPublishMap.putAll(mCurrentMap)

                mCurrentMap.clear()
                val iterator: Iterator<*> = mCurrentPublishMap.entries.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next() as Map.Entry<*, *>
                    allKey = entry.key as String?
                    allValue = mCurrentPublishMap[allKey]
                    stringBuffer.append(allValue)
                    stringBuffer.append("/")
                }
                // publishMessage(CLUB_ID + "/" + HEART_RATE_SYSTEM, stringBuffer.toString());
                handler.sendEmptyMessage(HANDLER_MESSAGES_CODE)
                LogUtil.e( """$CLUB_ID    size=${mCurrentPublishMap.size}
    publishMessage=$stringBuffer
    """.trimIndent()
                )


                publishMessage("$CLUB_ID/$HEART_RATE_SYSTEM", stringBuffer.toString())
                //                        publishMessage("1278950159512621058" + "/" + HEART_RATE_SYSTEM, stringBuffer.toString());
                stringBuffer.setLength(0) //清空
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSecondDisposable != null && !mSecondDisposable!!.isDisposed) {
            mSecondDisposable!!.dispose()
        }
        if (mSecondDisposableSn != null && !mSecondDisposableSn.isDisposed) {
            mSecondDisposableSn.dispose()
        }
        mTemperatureUsbControl!!.onPause()
        unregisterReceiver(mUsbReceiver)
        unregisterReceiver(mUsbPermissionActionReceiver)
        //        if(conn!=null){
//            unbindService(conn);
//            conn = null;
//        }
        if (isConnected) {
            disconnect()
        }
        EventBus.getDefault().unregister(this)
    }

    fun changeStatus(message: String?) {
        logStr.append(DateUtils.getStrTime(System.currentTimeMillis()))
        logStr.append(message)
        logStr.append("\n")
        mHandler.sendEmptyMessage(HANDLE_WHAT_TYPE)
    }

    /**
     * @param data
     */
    private fun updateReceivedData(data: ByteArray) {

        //累计字节数
        mTotalBytes = mTotalBytes + data.size


        //初始化字节数组
        /*  var res: ByteArray = ByteArray(2)
  //取高八位
          res[0] = (20000.toInt().ushr(8) and 0xFF).toByte()
          //取低位
          res[1] = (20000 and 0xFF).toByte()*/

        //var s = data.get(0) and 0x7F
        // Log.e(Companion.TAG, "analysisData : " + s)


        //ff 00 00 04 9e 2d 00 bf f7   ff 00 00 04 21 31 00 3c d7 ff 00 00 04 12 38 00 3c ed
        //0-27
        val receiveData = HexDump.bytesToHexString(data)

        Log.e(tgs, "--11-ant接收的数据=$receiveData")
        mCurrentDataLength = receiveData.length
        //        String receiveData = "ff 00 00 04 21 31 00 3c d7 ff 00 00 04 12 38 00 3c ed ff 00 00 04 12 38 00 3c ed ff 00 00 04 12 38 00 3c ed";
        if (TextUtils.isEmpty(receiveData)) {
            return
        }
        //  Log.e(Companion.TAG, "analysisData : " + receiveData)
        /* if (s == 0) {
             Log.e(Companion.TAG, "analysisData : " + receiveData)
         }*/
        val rawData = receiveData.split(" ").toTypedArray()

        Log.e(tgs, "--22-ant接收的数据=$"+ rawData.contentToString())

        var index = 1
        if (rawData.size > 9) {
            index = rawData.size / 9
        }
        /* Log.e(
             Companion.TAG,
             "data1 : $receiveData index=$index" + "resultData[1]=" + rawData[1] + "rawData.size=" + rawData.size
         )*/
        if (rawData.size == 9 && rawData[1].equals("55")) {
            analysisData(rawData)
        } else {
            for (i in 0 until index) {
                val resultData = Arrays.copyOfRange(rawData, i * 9, 9 * (i + 1))
                //        String time =  DateUtils.getStrTime(System.currentTimeMillis());
                //ff是实时数据，fe是历史数据
                if (resultData[1].equals("55")) {
                    analysisData(resultData)
                }
            }
        }
    }

    private fun analysisData(resultData: Array<String>) {


        try {
            //设备SN
            val devicesSN = StringBuffer()
            // devicesSN.append(resultData[1])
            devicesSN.append(resultData[2])
            devicesSN.append(resultData[3])
            devicesSN.append(resultData[4])
            var sn = devicesSN.toString().toInt(16).toString()
            //            sn = "000000" + sn;
            if (sn.length < 10) {
                val addSize = 10 - sn.length
                var zero = ""
                for (k in 0 until addSize) {
                    zero = zero + "0"
                }
                sn = zero + sn
            }


            //int sn = Integer.parseInt(devicesSN.toString(), 16);
            //设备电量
            val devicesEnergy = resultData[5]
            val energy = devicesEnergy.toInt(16)
            //心率
            val heartRateStr = resultData[7]
            val heartRate = heartRateStr.toInt(16)
            val str = sn + "_" + heartRate + "_" + energy

            Log.e(Companion.TAG, "str : " + str + "heartRateStr=" + heartRateStr)
            mCurrentMap[sn] = str
        } catch (e: Exception) {

        }

        /*resultData.forEach {
            Log.e(Companion.TAG, "analysisData : " + it)
        }*/


    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        private val TAG = MainActivity::class.java.simpleName
        private const val HANDLE_WHAT_TYPE = 1
        private const val SP_KEY_MQTT_CLUBINFO = "sp_key_mqtt_clubinfo"
        private const val HANDLER_MESSAGES_CODE = 111
    }
    // /**
    //     * @param data
    //     */
    //    private fun updateReceivedData(data: ByteArray) {
    //
    //        //累计字节数
    //        mTotalBytes = mTotalBytes + data.size
    //
    //
    //        //初始化字节数组
    //        /*  var res: ByteArray = ByteArray(2)
    //  //取高八位
    //          res[0] = (20000.toInt().ushr(8) and 0xFF).toByte()
    //          //取低位
    //          res[1] = (20000 and 0xFF).toByte()*/
    //
    //        var s = data.get(0) and 0x7F
    //        Log.e(Companion.TAG, "analysisData : " + s)
    //
    //
    //        //ff 00 00 04 9e 2d 00 bf f7   ff 00 00 04 21 31 00 3c d7 ff 00 00 04 12 38 00 3c ed
    //        //0-27
    //        val receiveData = HexDump.bytesToHexString(data)
    //        mCurrentDataLength = receiveData.length
    //        //        String receiveData = "ff 00 00 04 21 31 00 3c d7 ff 00 00 04 12 38 00 3c ed ff 00 00 04 12 38 00 3c ed ff 00 00 04 12 38 00 3c ed";
    //        if (TextUtils.isEmpty(receiveData)) {
    //            return
    //        }
    //        Log.e(Companion.TAG, "analysisData : " + receiveData)
    //        /* if (s == 0) {
    //             Log.e(Companion.TAG, "analysisData : " + receiveData)
    //         }*/
    //        val rawData = receiveData.split(" ").toTypedArray()
    //        var index = 1
    //        if (rawData.size > 9) {
    //            index = rawData.size / 9
    //        }
    //        Log.e(Companion.TAG, "data1 : $receiveData index=$index" + "resultData[1]=" + rawData[1] + "rawData.size=" + rawData.size)
    //        if (rawData.size == 9 && rawData[1].equals("55")) {
    //            analysisData(rawData)
    //        } else {
    //            for (i in 0 until index) {
    //                val resultData = Arrays.copyOfRange(rawData, i * 9, 9 * (i + 1))
    //                //        String time =  DateUtils.getStrTime(System.currentTimeMillis());
    //                //ff是实时数据，fe是历史数据
    //                if (resultData[1].equals("55")) {
    //                    analysisData(resultData)
    //                }
    //            }
    //        }
    //    }
    //
    //    private fun analysisData(resultData: Array<String>) {
    //
    //
    //        try {
    //            //设备SN
    //            val devicesSN = StringBuffer()
    //            // devicesSN.append(resultData[1])
    //            devicesSN.append(resultData[2])
    //            devicesSN.append(resultData[3])
    //            devicesSN.append(resultData[4])
    //            var sn = devicesSN.toString().toInt(16).toString()
    //            //            sn = "000000" + sn;
    //            if (sn.length < 10) {
    //                val addSize = 10 - sn.length
    //                var zero = ""
    //                for (k in 0 until addSize) {
    //                    zero = zero + "0"
    //                }
    //                sn = zero + sn
    //            }
    //
    //
    //            //int sn = Integer.parseInt(devicesSN.toString(), 16);
    //            //设备电量
    //            val devicesEnergy = resultData[5]
    //            val energy = devicesEnergy.toInt(16)
    //            //心率
    //            val heartRateStr = resultData[7]
    //            val heartRate = heartRateStr.toInt(16)
    //            val str = sn + "_" + heartRate + "_" + energy
    //
    //            Log.e(Companion.TAG, "str : " + str + "heartRateStr=" + heartRateStr)
    //            mCurrentMap[sn] = str
    //        } catch (e: Exception) {
    //
    //        }
    //
    //        /*resultData.forEach {
    //            Log.e(Companion.TAG, "analysisData : " + it)
    //        }*/
    //
    //
    //    }
}