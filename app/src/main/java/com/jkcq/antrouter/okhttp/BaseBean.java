package com.jkcq.antrouter.okhttp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/10/12.
 */
public class BaseBean implements Parcelable {

    private int code;

    private String message;

    private long timestamp;
    private String error;
    private String exception;
    private String path;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseBean() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.error);
        dest.writeString(this.exception);
        dest.writeString(this.path);
    }

    protected BaseBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.error = in.readString();
        this.exception = in.readString();
        this.path = in.readString();
    }

}