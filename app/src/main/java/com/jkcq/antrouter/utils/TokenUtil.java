package com.jkcq.antrouter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * 保存Token信息
 * 
 * @author mhj
 * Create at 2017/11/1 14:38 
 */
public class TokenUtil {

    private static final String TAG = TokenUtil.class.getSimpleName();

    private static final String TAG_TOKEN = "token";
    private static final String TAG_PEOPLE_ID = "peopleId";
    private static final String TAG_PHONE_NUM = "phoneNumber";

    private static final String TAG_CLUB_ID = "club_id";

    private static TokenUtil instance;

    public synchronized static TokenUtil getInstance() {
        if (null == instance) {
            synchronized (TokenUtil.class) {
                instance = new TokenUtil();
            }
        }
        return instance;
    }

    /**
     * 更新保存的Token
     *
     * @param context
     * @param token
     * @return
     */
    public void updateToken(Context context, String token) {
        if (null == context) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.commit();
    }

    /**
     * 获取Token
     *
     * @param context
     * @return
     */
    public String getToken(Context context) {
        if (null == context) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);

        return preferences.getString(TAG_TOKEN, "");
    }

    public void updatePeopleId(Context context, String peopleId) {
        if (null == context) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_PEOPLE_ID, peopleId);
        editor.commit();
    }

    public void savePhone(Context context, String phoneNum) {
        if (null == context) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_PHONE_NUM, phoneNum);
        editor.commit();
    }

    public String getPeopleId(Context context) {
        if (null == context) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        return preferences.getString(TAG_PEOPLE_ID, "1");
    }

    public String getPhone(Context context) {
        if (null == context) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        return preferences.getString(TAG_PHONE_NUM, "");
    }


    public void saveClubID(Context context, String clubid) {
        if (null == context) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_CLUB_ID, clubid);
        editor.commit();
    }

    public String getClubID(Context context) {
        if (null == context) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG,
                0);
        return preferences.getString(TAG_CLUB_ID, "");
    }

    public void clear(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(TAG, 0);
        preferences.edit().clear().commit();
        preferences.edit().remove(TAG_TOKEN).commit();
        preferences.edit().remove(TAG_PEOPLE_ID).commit();
        preferences.edit().remove(TAG_PHONE_NUM).commit();
        preferences.edit().remove(TAG_CLUB_ID).commit();

    }
}
