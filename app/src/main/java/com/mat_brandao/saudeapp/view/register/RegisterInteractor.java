package com.mat_brandao.saudeapp.view.register;

import android.content.Intent;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.mat_brandao.saudeapp.domain.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;


public interface RegisterInteractor {
    Observable<Response<ResponseBody>> requestCreateNormalUser(String name, String email, String sex,
                                                               String cep, long birthDate, String password);

    Observable<Response<ResponseBody>> requestCreateFacebookUser(String name, String email, String sex,
                                                                 String cep, long birthDate, String password);

    Observable<Response<ResponseBody>> requestCreateGoogleUser(String name, String email, String sex,
                                                               String cep, long birthDate, String password);

    Observable<Response<User>> requestLoginWithAccount(String email, String password);

    Observable<Response<User>> requestLoginWithFacebook(String email, String token);

    Observable<Response<User>> requestLoginWithGoogle(String email, String token);

    void saveUserToRealm(User user);

    User getUser();

    Observable<Response<ResponseBody>> requestSaveProfilePhoto(Integer avatarDrawable);
}