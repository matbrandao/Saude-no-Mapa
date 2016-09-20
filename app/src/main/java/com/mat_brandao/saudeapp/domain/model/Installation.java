package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Mateus Brandão on 20/09/2016.
 */

public class Installation extends RealmObject {
    @SerializedName("codApp")
    @Expose
    private long appId;
    @SerializedName("codUsuario")
    @Expose
    private long userId;
    @SerializedName("dataHora")
    @Expose
    private String dateTime;
    @SerializedName("deviceOS")
    @Expose
    private String deviceOS;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
