package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mateus Brandão on 07-Sep-16.
 */

public class Error401 {
    private String url;
    private int statusCode;
    private String reasonPhrase;
    @SerializedName("mensagens")
    private List<ErrorMessage> messageList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public List<ErrorMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ErrorMessage> messageList) {
        this.messageList = messageList;
    }
}
