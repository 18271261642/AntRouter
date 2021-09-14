package com.jkcq.antrouter.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jkcq.antrouter.okhttp.BaseBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * GSON 工具类
 */
public class JsonUtils {

    private volatile static JsonUtils instance;

    public synchronized static JsonUtils getInstance() {
        if (instance == null) {
            synchronized (JsonUtils.class) {
                if (instance == null) {
                    synchronized (JsonUtils.class) {
                        instance = new JsonUtils();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 对象解析
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T parseObject(String json, Class<T> clazz) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Gson gson = new Gson();
            if (!jsonObject.has("obj")) {
                return gson.fromJson(json, clazz);
            }

            Object obj = jsonObject.get("obj");
            if (null == obj || TextUtils.isEmpty(obj.toString()) || TextUtils.equals(obj.toString(), "null") || TextUtils.equals(obj.toString(), "NULL")) {
                return gson.fromJson(json, clazz);
            }
            T t;
            if (obj instanceof JSONObject) {
                t = gson.fromJson(obj.toString(), clazz);
            } else {
                t = gson.fromJson(jsonObject.toString(), clazz);
            }

            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            if (t != null && t instanceof BaseBean) {
                BaseBean baseBean = (BaseBean) t;
                baseBean.setCode(code);
                baseBean.setMessage(message);
                return (T) baseBean;
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("json数据有误：" + e.getMessage());
        }
        return null;
    }

    public <T> T parseObject(String json, Type clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
//            DebugLog.e("json数据有误：" + e.getMessage());
            return null;
        }
    }
    public static <T> List<T> StringTolist(String jsonString, Class<T[]> type) {

        Gson gson = new Gson();
        T[] list = gson.fromJson(jsonString, type);

        return Arrays.asList(list);
    }

        public <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<List<T>>() {
            }.getType());


        } catch (Exception e) {
            e.printStackTrace();
//            DebugLog.e("json数据有误：" + e.getMessage());
            return null;
        }
    }

    public String toJSON(Object obj) {
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValueByKey(JSONObject obj, String key) {
        String value = "";
        if (null != obj && !TextUtils.isEmpty(key)) {
            try {
                value = obj.has(key) ? obj.getString(key) : "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public String getValueByKey(JSONObject obj, String key, String def) {
        String value = def;
        if (null != obj && !TextUtils.isEmpty(key)) {
            try {
                value = obj.has(key) ? obj.getString(key) : def;
                value = TextUtils.isEmpty(key) ? def : value;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
