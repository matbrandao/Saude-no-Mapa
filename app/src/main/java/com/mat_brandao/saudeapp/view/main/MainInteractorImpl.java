package com.mat_brandao.saudeapp.view.main;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class MainInteractorImpl implements MainInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public MainInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}