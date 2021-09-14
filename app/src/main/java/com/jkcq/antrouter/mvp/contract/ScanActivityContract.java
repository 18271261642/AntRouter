package com.jkcq.antrouter.mvp.contract;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/4.
 */

public interface ScanActivityContract {
    interface Model {
    }

    interface BaseIView {
        /**成功*/
        void onSuccess(byte[] data);
        /**测试心电成功*/
        void onEcgSuccess(ArrayList<String> heartRates, ArrayList<String> ecgADs);
        /**出错*/
        void onError(byte[] data, String tips);
        /**显示编辑框*/
        void showEdit();
    }

    interface Presenter {
        /**开始检测*/
        void startPhysical();
        /**接收数据回调*/
        void onDataReceive(byte[] data);
        /*退出*/
        void exit();
    }
}
