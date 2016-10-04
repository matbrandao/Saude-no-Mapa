package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class EditProfileInteractorImpl implements EditProfileInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public EditProfileInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}