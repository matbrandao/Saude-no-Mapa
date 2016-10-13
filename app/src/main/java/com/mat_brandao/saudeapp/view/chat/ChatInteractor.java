package com.mat_brandao.saudeapp.view.chat;

import com.mat_brandao.saudeapp.domain.model.User;

public interface ChatInteractor {
    boolean isMessageValid(String message);

    User getUser();

    String getPhotoUrl(Long userId);
}