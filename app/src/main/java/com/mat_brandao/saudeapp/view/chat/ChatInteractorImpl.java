package com.mat_brandao.saudeapp.view.chat;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class ChatInteractorImpl implements ChatInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public ChatInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}