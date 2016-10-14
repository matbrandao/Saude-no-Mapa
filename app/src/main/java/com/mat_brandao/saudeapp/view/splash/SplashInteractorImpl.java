package com.mat_brandao.saudeapp.view.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class SplashInteractorImpl implements SplashInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final String FIRST_USE_KEY = "first_use";
    private final SharedPreferences mSharedPreferences;

    public SplashInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
    }

    @Override
    public boolean isFirstUse() {
        // TODO: 14/10/2016
        return mSharedPreferences.getBoolean(FIRST_USE_KEY, true);
//        return true;
    }

    @Override
    public void setNotFirstUse() {
        mSharedPreferences.edit()
                .putBoolean(FIRST_USE_KEY, false)
                .apply();
    }
}