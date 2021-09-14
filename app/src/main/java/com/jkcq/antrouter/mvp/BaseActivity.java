package com.jkcq.antrouter.mvp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkcq.antrouter.AllocationApi;
import com.jkcq.antrouter.R;
import com.jkcq.antrouter.utils.CountTimer;
import com.jkcq.antrouter.utils.NetUtils;


/**
 * @author xsl
 * @version 1.0
 * @date 2017/4/19
 * @description
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected TextView navTitleText, tvAction;
    protected ImageView imageAction;
    protected Context mContext;
    public final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        init();
        ActivityManager.getInstance().putActivity(TAG, this);
    }


    /**
     * 获取layout Id
     *
     * @return layout Id
     */
    //protected abstract int getLayoutId();


    /**
     * 初始化toolbar
     */
    protected void initToolbar() {
        // toolbar = findViewById(R.id.toolbar);
        navTitleText = findViewById(R.id.mTitle);
        // tvAction = toolbar.findViewById(R.id.tvAction);
        //imageAction = toolbar.findViewById(R.id.imageAction);
        //toolbar.setNavigationIcon(R.mipmap.back);
        // toolbar.setTitle("");
        //toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        // navTitleText.setTextColor(getResources().getColor(R.color.whiteColor));
        // tvAction.setTextColor(getResources().getColor(R.color.whiteColor));
        // setSupportActionBar(toolbar);
    }


    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().removeActivity(TAG);
        super.onDestroy();

    }

    /**
     * 启动Activity
     * @param mClass
     */
    protected void startActy(Class mClass){
        startActivity(new Intent(getApplicationContext(),mClass));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(option);
//        }
    }

    /**
     * 是否需要状态栏，不需要布局将会延伸到状态栏
     * @param isNeed
     */
    protected void initStatusBar(boolean isNeed) {
        if (isNeed) {
            //需要用到系统状态栏，统一设置成黑色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.BLACK);
            }

        } else {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
//                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
//            View v = getCurrentFocus();
//
//            if (isShouldHideInput(v, ev)) {
//                hideSoftInput(v.getWindowToken());
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void netError(final Activity activity) {
        if (!AllocationApi.isNetwork && !AllocationApi.isShowHint) {
            // 显示Dialog
            AllocationApi.isShowHint = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("提示 \n\n目前处于无网络状态，是否立即开启网络！").setCancelable(false)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            NetUtils.openNet(activity);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private CountTimer countTimerView;

    private void timeStart(){
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                countTimerView.start();
            }
        });
    }
    private void init() {
        //初始化CountTimer，设置倒计时为2分钟。
        countTimerView=new CountTimer(5*60*1000,1000,BaseActivity.this);
    }

    /**
     * 主要的方法，重写dispatchTouchEvent
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        switch (ev.getAction()){
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                countTimerView.start();
                break;
            //否则其他动作计时取消
            default:countTimerView.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onPause() {
        super.onPause();
        countTimerView.cancel();
    }
    @Override
    protected void onResume() {
        super.onResume();
        timeStart();
    }

}