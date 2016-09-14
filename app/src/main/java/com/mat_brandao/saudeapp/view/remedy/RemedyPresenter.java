package com.mat_brandao.saudeapp.view.remedy;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface RemedyPresenter extends BasePresenter {
    void onScanBarcodeFabClick();

    void onScanSuccess(String data);
}