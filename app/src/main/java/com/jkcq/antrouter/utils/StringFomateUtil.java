package com.jkcq.antrouter.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

public class StringFomateUtil {

    public static enum FormatType {
        /**
         * 如：180.65 小数点后字体变小
         */
        Pointer,
        /**
         * 如： 2小时30分钟，单位字体变小
         */
        Time,
        /**
         * 如：10次，单位变小
         */
        Alone,
        /**
         * 秒钟变小 00：09：08 08变小
         */
        Second,
        /**
         * 其他的文字变小
         * 实际深睡时长7时32分，占总睡眠时长45
         */
        SleepTime,

        /**
         * 距离8.8公里
         */
        BandAnayze,
        /**
         * 距离8.8公里
         */
        BandAnayzeSinle

    }

    public static void formatText(FormatType formatType, Context context, TextView tv, int color, int strRes, String... args) {
        switch (formatType) {
            case Pointer: {
                String str = UIUtils.getContext().getString(strRes, args[0]);
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("."), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setTextColor(color);
                break;
            }
            case Time: {
                String day = args[0];
                String hours = args[1];
                // float scale = Float.valueOf(args[1]);
			/*int mins = Integer.valueOf(minutes);
			String hour = String.valueOf(mins / 60);
			String minute = String.valueOf(mins % 60);*/
                String str = UIUtils.getContext().getString(strRes, day, hours);
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(0.5f), day.length(), str.lastIndexOf(hours),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(0.5f), str.lastIndexOf(hours) + hours.length(), str.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setTextColor(color);
                break;
            }
            case SleepTime: {
                String day = args[0];
                String hours = args[1];
                String rate = args[2];
                float scale = Float.valueOf(args[1]);
			/*int mins = Integer.valueOf(minutes);
			String hour = String.valueOf(mins / 60);
			String minute = String.valueOf(mins % 60);*/


                String str = UIUtils.getContext().getString(strRes, day, hours, rate);
                int startIndex = 0;
                int endInext = str.indexOf(day);
                int oneLen, twoLen, threeLen;
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(0.7f), startIndex, endInext,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                oneLen = endInext + day.length();

                String strTemp = str.substring(oneLen, str.length());


                twoLen = strTemp.indexOf(hours);
                span.setSpan(new RelativeSizeSpan(0.7f), oneLen, twoLen + oneLen,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(0.7f), twoLen + oneLen + hours.length(), str.lastIndexOf(rate),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(0.7f), str.lastIndexOf(rate) + rate.length(), str.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                AssetManager assets = UIUtils.getContext().getAssets();
                Typeface font = Typeface.createFromAsset(assets, "fonts/BebasNeue.otf");
                tv.setText(span);
                tv.setTextColor(color);
                break;
            }
            case Alone: {
                String temp = args[0];
                float scale = Float.valueOf(args[1]);
                String str = UIUtils.getContext().getString(strRes, temp);
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(scale), str.indexOf(temp) + temp.length(), str.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //span.setSpan(new BackgroundColorSpan(Color.RED), str.indexOf(temp) + temp.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  //红色高亮
                tv.setText(span);
                tv.setTextColor(color);
                //tv.setTextColor();
                break;
            }
            case Second: {
                String temp = args[0];

                SpannableString span = new SpannableString(temp);
                span.setSpan(new RelativeSizeSpan(0.5f), temp.length() - 3, temp.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setTextColor(color);
                //String s[] = temp.split(":");
                break;
            }
            case BandAnayze: {
                String value = args[0];
                float scale = Float.valueOf(args[1]);
                String str = UIUtils.getContext().getString(strRes, value);
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(scale), 0, str.lastIndexOf(value),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(scale), str.lastIndexOf(value) + value.length(), str.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setTextColor(color);
                break;
            }
            case BandAnayzeSinle: {
                String value = args[0];
                float scale = Float.valueOf(args[1]);
                String str = UIUtils.getContext().getString(strRes, value);
                SpannableString span = new SpannableString(str);
                span.setSpan(new RelativeSizeSpan(scale), 0, str.lastIndexOf(value),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
                tv.setTextColor(color);
                break;
            }
            default:
                break;
        }
    }

    public static void setSleepTime(Context context, TextView tv, int strRes, int minute) {
        String scale = "0.3";
        //formatText(FormatType.Time, context, tv, strRes, String.valueOf(minute), scale);
    }

}
