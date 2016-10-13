package com.mat_brandao.saudeapp.view.my_groups;

import android.content.Context;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class MyGroupsInteractorImpl implements MyGroupsInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public MyGroupsInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public Observable<Response<List<Grupo>>> requestMyGroups() {
        return RestClient.getHeader(mUserRepository.getUser().getAppToken(), null)
                .getGroups(mContext.getString(R.string.app_id), null, mUserRepository.getUser().getId());
    }
}