package com.jkcq.antrouter.bean;

/*
 *
 *
 * @author mhj
 * Create at 2018/11/30 16:37
 */

import java.util.ArrayList;

public class HeartBean {

    private String SNId;
    private int power;
    private int heart;
    private String time;
    private  long  longTime;
    private int Count  = 0;

    private ArrayList<ContentBean> contentBeans = new ArrayList<>();

    public String getSNId() {
        return SNId;
    }

    public void setSNId(String SNId) {
        this.SNId = SNId;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public long getLongTime() {
        return longTime;
    }

    public void setLongTime(long longTime) {
        this.longTime = longTime;
    }

    public int getCount(){
        return Count;
    }

    public void setCount() {
        Count = Count+1;
    }

    public ArrayList<ContentBean> getContentBeans() {
        return contentBeans;
    }

    public void setContentBeans(ContentBean contentBean){
        if(contentBeans== null){
            contentBeans = new ArrayList<>();
        }
        contentBeans.add(contentBean);
    }
}
