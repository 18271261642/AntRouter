
package com.jkcq.antrouter.activity;


import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:NetProgressBar <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2017年4月19日 上午11:31:46 <br/>
 *
 * @author Administrator
 */
public class HrDataObservable extends Observable {

    private static HrDataObservable obser;


    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    private HrDataObservable() {
        super();
    }

    public static HrDataObservable getInstance() {
        if (obser == null) {
            synchronized (HrDataObservable.class) {
                if (obser == null) {
                    obser = new HrDataObservable();
                }
            }
        }
        return obser;
    }


    public void sendAllHrData(ConcurrentHashMap<String, Integer> data) {
        HrDataObservable.getInstance().setChanged();
        HrDataObservable.getInstance().notifyObservers(data);

    }

}