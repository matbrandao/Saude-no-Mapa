package com.mat_brandao.saudeapp.view.register;

import android.content.Context;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class RegisterInteractorImpl implements RegisterInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public RegisterInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateNormalUser(String name, String email, String sex,
                                                                      String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, email, password, null, null, cep, sex));
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateFacebookUser(String name, String email, String sex,
                                                                        String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, email, null, password, null, cep, sex));
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateGoogleUser(String name, String email, String sex,
                                                                      String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, email, null, null, password, cep, sex));
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
    }
}