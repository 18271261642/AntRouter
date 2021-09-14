package com.jkcq.antrouter.utils;

import android.graphics.Paint;
import android.graphics.Rect;

public class TextUtil {
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return ((int) Math.ceil(fm.descent - fm.top) + 2) / 2;
    }

    public static int getFontHeight(String mGraduationTitle) {
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.getTextBounds(mGraduationTitle, 0, mGraduationTitle.length() - 1, rect);
        return rect.height(); //获得的宽度似乎不准实际情况
    }

    public static float getFontWidth(String txt, Paint paint) {
        return paint.measureText(txt);
    }

}
