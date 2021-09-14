package com.jkcq.antrouter.mvp.presenter;


import com.jkcq.antrouter.bean.ClubBean;
import com.jkcq.antrouter.bean.ClubInfo;
import com.jkcq.antrouter.mvp.BasePresenter;
import com.jkcq.antrouter.mvp.view.MainActivityView;

/*
 *
 *
 * @author mhj
 * Create at 2019/2/16 17:29
 */
public class MainActivityPresenter extends BasePresenter<MainActivityView> implements MainActivityView {





    @Override
    public void getClubSuccess(ClubBean clubBean) {
        if(isViewAttached()){
            mActView.get().getClubSuccess(clubBean);
        }
    }


    @Override
    public void registerSuccess(boolean flag) {
        if(isViewAttached()){
            mActView.get().registerSuccess(flag);
        }
    }

    @Override
    public void onRespondError(String message) {
        if(isViewAttached()){
            mActView.get().onRespondError(message);
        }
    }



    @Override
    public void upStatusSuccess(boolean isSuccess) {
        if(isViewAttached()){
            mActView.get().upStatusSuccess(isSuccess);
        }
    }


    @Override
    public void getRegisterClub(ClubInfo info) {
        if(isViewAttached()){
            mActView.get().getRegisterClub(info);
        }
    }
}
