package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class FavRemedyInteractorImpl implements FavRemedyInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public FavRemedyInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}