package com.jkcq.antrouter.usbserial;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.gson.Gson;
import com.jkcq.antrouter.AntRouterApplication;
import com.jkcq.antrouter.listener.AntReceiveListener;

import com.jkcq.antrouter.usbserial.driver.UsbSerialDriver;
import com.jkcq.antrouter.usbserial.driver.UsbSerialPort;
import com.jkcq.antrouter.usbserial.driver.UsbSerialProber;
import com.jkcq.antrouter.usbserial.util.SerialInputOutputManager;
import com.jkcq.antrouter.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class TemperatureUsbControl {

    private static final String TAG = "shao";
//    private static final int TEMPERATURE_USB_VENDOR_ID = 1003;     //供应商id
//    private static final int TEMPERATURE_USB_PRODUCT_ID = 24857;    //产品id

    private static final int TEMPERATURE_USB_VENDOR_ID = 1003;     //供应商id
    private static final int TEMPERATURE_USB_PRODUCT_ID = 24857;    //产品id

    //30034
    private static final int TEM_USB_VENDOR_3034_ID =6421;
    private static final int TEM_USB_PRODUCT_3034_ID = 24857;

    private Context mContext;
    private UsbManager mUsbManager; //USB管理器
    private UsbSerialPort sTemperatureUsbPort = null;  //接体温枪的usb端口
    private SerialInputOutputManager mSerialIoManager;  //输入输出管理器（本质是一个Runnable）
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();  //用于不断从端口读取数据

    private Disposable mDisposable;
    private boolean isStop = false;

    //数据输入输出监听器
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.e(TAG, "------Runner stopped="+e.getMessage());
                    listener.onRunError(e);
                }

                @Override
                public void onNewData(final byte[] data) {
                    Log.e(TAG, "-----new data="+ Utils.formatHex(data));
                    listener.onNewData(data);
                }
            };

    public TemperatureUsbControl(Context context) {
        mContext = context;
    }

    public void initUsbControl() {
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        //全部设备
        List<UsbSerialDriver> usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);


//        List<UsbSerialDriver> tempUsb = UsbSerialProber.getCus().findAllDrivers(mUsbManager);
        Map<String,UsbDevice> tmepList = mUsbManager.getDeviceList();


        String initUsb = new Gson().toJson(usbSerialDrivers);
        Log.e(TAG,"-----初始化USB="+initUsb);

        AntRouterApplication.getApp().setStringBuilder(initUsb);

        //全部端口
        List<UsbSerialPort> usbSerialPorts = new ArrayList<>();

        for (UsbSerialDriver driver : usbSerialDrivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            Log.e(TAG, String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));

            Timber.e("--------getPorts="+driver.getClass().getName()+" "+driver.getDevice().getVendorId()+" "+driver.getDevice().getProductId());
            usbSerialPorts.addAll(ports);
        }

        int vendorId;
        int productId;
        //校验设备，设备是 2303设备
        for (UsbSerialPort port : usbSerialPorts) {
            UsbSerialDriver driver = port.getDriver();
            UsbDevice device = driver.getDevice();
//            vendorId = HexDump.toHexString((short) device.getVendorId());
//            productId = HexDump.toHexString((short) device.getProductId());
            vendorId = device.getVendorId();
            productId = device.getProductId();
            Timber.e("------vendorId="+vendorId+" productId="+productId+"\n"+(vendorId == TEM_USB_VENDOR_3034_ID)+"\n"+(productId ==TEM_USB_PRODUCT_3034_ID ));
            boolean isGet = vendorId == TEM_USB_VENDOR_3034_ID && productId == TEM_USB_PRODUCT_3034_ID;
            Timber.e("------isGet="+isGet+" "+(port == null));
            if ((vendorId == TEMPERATURE_USB_VENDOR_ID && productId == TEMPERATURE_USB_PRODUCT_ID) || isGet) {
                sTemperatureUsbPort = port;

            }
        }


        if (sTemperatureUsbPort != null) {
            //成功获取端口，打开连接
            UsbDeviceConnection connection = mUsbManager.openDevice(sTemperatureUsbPort.getDriver().getDevice());

            Timber.e("-----------成功获取端口，打开链接"+(connection == null));

            if (connection == null) {
                Log.e(TAG, "Opening device failed");
                return;
            }
            try {

                sTemperatureUsbPort.open(connection);
                //设置波特率  115200    9600
                sTemperatureUsbPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                sTemperatureUsbPort.setRTS(true);
                sTemperatureUsbPort.setDTR(true);
            } catch (Exception e) {
                e.printStackTrace();
                //打开端口失败，关闭！
                Log.e(TAG, "------Error setting up device: " + e.getMessage(), e);
                try {
                    sTemperatureUsbPort.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                    // Ignore.
                }
                sTemperatureUsbPort = null;
                return;
            }
        } else {
            //提示未检测到设备
        }
    }


    public void onDeviceStateChange() {
        //重新开启USB管理器
        stopIoManager();
        startIoManager();
    }

    private void startIoManager() {
        if (sTemperatureUsbPort != null) {
            Log.e(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sTemperatureUsbPort, mListener);
            isStop = false;
            sendCommond();
            mExecutor.submit(mSerialIoManager);  //实质是用一个线程不断读取USB端口
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.e(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    public void onPause() {
        isStop = true;
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        stopIoManager();
        if (sTemperatureUsbPort != null) {
            try {
                sTemperatureUsbPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sTemperatureUsbPort = null;
        }
    }

    public void sendCommond() {

        //每隔20s发送一次命令
        mDisposable = Observable.interval(0, 20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // 由于interval默认在新线程，所以我们应该切回主线程
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        try {

                            Log.e("sendCommond", "sendCommond-------");

                            byte[] data = null;
                            if (isStop) {
                                //结束命令
                                Log.e("shao", "-----send stop cmd");
                                data = new byte[]{0x02, (byte) 0xFE, (byte) 0xA2, 0x74, 0x69, (byte) 0xFE, (byte) 0xA2, 0x74, 0x69, 0x03};
                            } else {
                                //开始命令
                               // 邓工 02 a2 74 69 fe a2 74 69 fe 03 心率请的指令
                                data = new byte[]{0x02, (byte) 0xA2, 0x74, 0x69, (byte) 0xFE, (byte) 0xA2, 0x74, 0x69, (byte) 0xFE, 0x03};
                                //02 4d 5a 2d 53 54 41 52 54 03 8888的指令
                                // data = new byte[]{0x02, (byte) 0x4d, 0x5a, 0x2d, (byte) 0x53, (byte) 0x54, 0x41, 0x52, (byte) 0x54, 0x03};
                                //秦总
                                // 02 4D 5A 2D 53 54 41 52 54 03
//                                data = new byte[]{0x02, (byte)0x4D, 0x5A, 0x2D, (byte)0x53, (byte)0x54, 0x41, 0x52, (byte)0x54, 0x03};

//                                data = new byte[]{0x02,0x4D,0x5A,0x2D,0x53,0x54,0x41,0x52,0x54,0x03};

                            }
                            if (sTemperatureUsbPort != null) {
                                sTemperatureUsbPort.write(data, 200);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private AntReceiveListener listener;

    public void setAntReceiveListener(AntReceiveListener listener) {
        this.listener = listener;
    }



}