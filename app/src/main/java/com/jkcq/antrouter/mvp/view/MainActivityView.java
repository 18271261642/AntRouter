package com.jkcq.antrouter.mvp.view;


import com.jkcq.antrouter.bean.ClubBean;
import com.jkcq.antrouter.bean.ClubInfo;
import com.jkcq.antrouter.mvp.BaseView;

/*
 *
 *
 * @author mhj
 * Create at 2019/2/16 17:24
 */public interface MainActivityView extends BaseView {

     void getClubSuccess(ClubBean clubBean);

     void registerSuccess(boolean flag);

     void upStatusSuccess(boolean isSuccess);

     void  getRegisterClub(ClubInfo info);
}

