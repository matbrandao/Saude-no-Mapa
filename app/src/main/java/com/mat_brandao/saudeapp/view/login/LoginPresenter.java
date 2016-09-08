package com.mat_brandao.saudeapp.view.login;

import android.content.Intent;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface LoginPresenter extends BasePresenter {
    void forgotPasswordClicked();

    void onLoginWithAccountTextClicked();

    void facebookLoginClicked();

    void normalLoginClicked(String email, String password);

    void registerButtonClicked();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}