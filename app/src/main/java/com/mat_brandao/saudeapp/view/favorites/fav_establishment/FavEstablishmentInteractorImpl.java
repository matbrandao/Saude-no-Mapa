package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class FavEstablishmentInteractorImpl implements FavEstablishmentInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public FavEstablishmentInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }
}