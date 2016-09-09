package com.mat_brandao.saudeapp.view.login;

import android.content.Intent;
import android.widget.EditText;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.OnFormEmitted;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Completable;
import rx.Observable;

public interface LoginInteractor {
    void validateForms(EditText emailEdit, EditText passEdit, OnFormEmitted listener);

    void requestLoginToFacebook(List<String> permissions, FacebookCallback<LoginResult> callback);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    Observable<Response<User>> requestLoginWithAccount(String email, String password);

    Observable<Response<User>> requestLoginWithFacebook(String email, String token);

    Observable<Response<User>> requestLoginWithGoogle(String email, String token);

    Observable<Response<ResponseBody>> requestRememberPassword(String email);

    void saveUserToRealm(User user);

    void clearUsers();
}