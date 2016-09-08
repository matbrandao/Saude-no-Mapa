package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mateus Brandão on 07-Sep-16.
 */
public class ErrorMessage {
    @SerializedName("texto")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
