package com.mat_brandao.saudeapp.view.chat;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface ChatPresenter extends BasePresenter {
    void onSendButtonClick(String message);
}