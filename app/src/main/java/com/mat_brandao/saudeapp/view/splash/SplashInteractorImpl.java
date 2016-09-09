package com.mat_brandao.saudeapp.view.splash;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class SplashInteractorImpl implements SplashInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public SplashInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
    }
}