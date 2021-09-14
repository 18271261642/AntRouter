package com.jkcq.appupdate;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import java.math.BigDecimal;

public class AppUpdateUtils {

    public static boolean isUpdateMandatory=false;
    public static boolean isDownLoad=false;

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        } else {
            Point point = new Point();
            if (Build.VERSION.SDK_INT >= 17) {
                wm.getDefaultDisplay().getRealSize(point);
            } else {
                wm.getDefaultDisplay().getSize(point);
            }

            return point.x;
        }
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
