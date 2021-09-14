package com.jkcq.antrouter.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.jkcq.antrouter.AntRouterApplication;
import com.jkcq.antrouter.utils.DeviceUtil;
import com.jkcq.antrouter.utils.LogUtil;
import com.jkcq.antrouter.utils.NetUtils;
import com.jkcq.antrouter.utils.SavePreferencesData;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc         ${MQTT服务}
 */

public class MyMqttService extends BaseMqttservice {

    public final String TAG = MyMqttService.class.getSimpleName();
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    //public        String HOST           = "tcp://192.168.10.102:61613";//服务器地址（协议+地址+端口号）
    // public String HOST = "tcp://120.77.62.190:61613";//服务器地址（协议+地址+端口号）
    public static String HOST = "tcp://mqtt.fitalent.com.cn:61613";//服务器地址（协议+地址+端口号）
    public String USERNAME = "admin";//用户名
    public String PASSWORD = "fitalent@1.";//密码
    public static String PUBLISH_TOPIC = "";//发布主题
    public static String RESPONSE_TOPIC = "message_arrived";//响应主题
    public String CLIENTID = DeviceUtil.getMac(AntRouterApplication.getApp()) + "hall";
    ;//客户端ID，一般以客户端唯一标识符表示，这里用设备序列号表示


    //每一分钟
    private String path = "";
    private int mTotalBytes = 0;//接收到的总字节数
    private String current_time = "";


    private static int PARSE_DATA = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        //开始解析线程

        SavePreferencesData savePreferencesData = new SavePreferencesData(getApplication());
        String clubId = savePreferencesData.getStringData(SavePreferencesData.SP_KEY_MQTT_CLUBINFO);
        if (!TextUtils.isEmpty(clubId)) {
            PUBLISH_TOPIC = clubId + "/HEART_RATE_SYSTEM";
//            PUBLISH_TOPIC = 10000 + "/HEART_RATE_SYSTEM";
        } else {
            PUBLISH_TOPIC = "10000" + "/HEART_RATE_SYSTEM";
        }
        // PUBLISH_TOPIC = "10000" + "/HEART_RATE_SYSTEM";
        init();
        //初始化Log写入工具
        //FileUtil.initFile(BaseApp.getApp());
        //path = FileUtil.getFile();
        // current_time = DateUtils.getStrTimes(System.currentTimeMillis());
        // }
        // LogUtil.i(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
//        String topic = intent.getStringExtra(PUBLISH_TOPIC_KEY);
//        if (!TextUtils.isEmpty(topic)) {
//            mPublisTopic = topic;
//        }
//        init();
        return new MyMqttServiceBinder();
    }


    /**
     * 发布 （模拟其他客户端发布消息）
     *
     * @param message 消息
     */
    public static void publish(String message) {
        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     *
     * @param message 消息
     */
    public void response(String message) {
        String topic = RESPONSE_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(this, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(60); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Log.e(TAG, "topic=" + mPublisTopic + "message=" + message);
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                LogUtil.i(TAG, "Exception Occured" + e.toString());
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }


    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!isEnd) {
            try {
                if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
                    try {
                        mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            LogUtil.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            LogUtil.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            LogUtil.i(TAG, "连接成功 订阅主题为 :" + PUBLISH_TOPIC + "#");
            try {
                mqttAndroidClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
            } catch (Exception e) {
                //todo mqttAndroidClient will be null
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            LogUtil.i(TAG, "连接失败 ");
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (NetUtils.hasNetwork(getApplicationContext())) {
                        e.onNext("");
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    LogUtil.i(TAG, "重连中.... ");
                    doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
    };


    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LogUtil.i(TAG, "topic= " + topic + " 收到消息： " + new String(message.getPayload()));
            /*Message msg = Message.obtain();
            msg.what = PARSE_DATA;
            msg.obj = new String(message.getPayload());
            mParseHandler.sendMessage(msg);*/
//            parseMqttData(new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            LogUtil.i(TAG, "连接断开 ");
            doClientConnection();//连接断开，重连
        }
    };


    //解析MQtt数据

    //解析单个心率数据


    //掉线移除用户信息
    @Override
    public void removeOffLine(String sn) {
    }


    public class MyMqttServiceBinder extends Binder {
        public MyMqttService getCollectionServices() {
            return MyMqttService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.i(TAG, "onUnbind");
        try {
            if (mqttAndroidClient != null) {
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient.disconnect(); //断开连接
                mqttAndroidClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isEnd = true;
        release();
        mqttCallback = null;
        return super.onUnbind(intent);
    }
}