package com.jkcq.antrouter.bean;

import com.jkcq.antrouter.okhttp.BaseBean;

/*
 *
 *
 * @author mhj
 * Create at 2019/1/29 18:31
 */
public class LoginConfirBean extends BaseBean {

     private boolean confirmation;

    public boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
}
