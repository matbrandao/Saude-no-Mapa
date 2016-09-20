package com.mat_brandao.saudeapp.view.main;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class MainInteractorImpl implements MainInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;

    public MainInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public Observable<Response<ResponseBody>> requestUserImage() {
        return RestClient.getHeader(mUser.getAppToken())
                .getProfilePhoto(mUser.getId());
    }

    @Override
    public String getProfilePhotoUrl() {
        return "http://mobile-aceite.tcu.gov.br/appCivicoRS/rest/pessoas/" + mUser.getId() + "/fotoPerfil.png";
    }

    @Override
    public void logout() {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.delete(User.class);
        });
    }
}