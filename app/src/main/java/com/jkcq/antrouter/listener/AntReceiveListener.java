package com.jkcq.antrouter.listener;

/*
 *
 *
 * @author mhj
 * Create at 2018/12/5 11:38
 */

public interface AntReceiveListener {

    void onNewData(byte[] data);

    void onRunError(Exception e);
}
