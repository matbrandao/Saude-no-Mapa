package com.mat_brandao.saudeapp.view.remedy;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class RemedyInteractorImpl implements RemedyInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private User mUser;

    public RemedyInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
    }

    @Override
    public Observable<Response<List<Remedy>>> requestRemediesByBarCode(String barcode) {
        // TODO: 13/09/2016 add token
        return RestClient
//                .getHeader(mUser.getAppToken())
                .get()
                .getRemedies(barcode);
    }
}