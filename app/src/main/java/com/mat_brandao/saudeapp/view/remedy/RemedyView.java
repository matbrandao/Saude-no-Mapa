package com.mat_brandao.saudeapp.view.remedy;

import android.content.Intent;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface RemedyView extends BaseView.BaseProgressView {
    void gotoActivityWithResult(Intent intent);
}