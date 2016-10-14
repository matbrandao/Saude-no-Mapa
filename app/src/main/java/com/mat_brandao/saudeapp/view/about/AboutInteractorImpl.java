package com.mat_brandao.saudeapp.view.about;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class AboutInteractorImpl implements AboutInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public AboutInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}