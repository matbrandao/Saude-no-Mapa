package com.mat_brandao.saudeapp.view.login;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.OnFormEmitted;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class LoginInteractorImpl implements LoginInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private CallbackManager mCallbackmanager;

    public LoginInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public void validateForms(EditText emailEdit, EditText passEdit, OnFormEmitted listener) {
        Observable<Boolean> emailObservable = RxTextView.textChanges(emailEdit)
                .map(inputText -> inputText.toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
                .skip(1)
                .distinctUntilChanged();

        Observable<Boolean> passwordObservable = RxTextView.textChanges(passEdit)
                .map(inputText -> inputText.toString().length() < 6)
                .skip(1)
                .distinctUntilChanged();

        emailObservable.subscribe(listener::emailOnNext);

        passwordObservable.subscribe(listener::passwordOnNext);

        Observable.combineLatest(
                emailObservable,
                passwordObservable,
                (emailValid, passValid) -> emailValid && !passValid)
                .distinctUntilChanged()
                .subscribe(listener::buttonChanged);
    }

    @Override
    public void requestLoginToFacebook(List<String> permissions, FacebookCallback<LoginResult> callback) {
        mCallbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance()
                .logInWithReadPermissions((LoginActivity) mContext, permissions);
        LoginManager.getInstance()
                .registerCallback(mCallbackmanager, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Observable<Response<User>> requestLoginWithAccount(String email, String password) {
        return RestClient.get()
                .login(email, password);
    }

    @Override
    public Observable<Response<ResponseBody>> requestRememberPassword(String email) {
        // TODO: 07-Sep-16  
        return null;
//        return RestClient.get()
//                .rememberPassword(email);
    }

    @Override
    public void saveUserToRealm(User user) {
        mUserRepository.clearUsers();
        mUserRepository.saveUser(user);
    }

    @Override
    public void clearUsers() {
        mUserRepository.clearUsers();
    }

    @Override
    public Observable<Response<User>> requestLoginWithFacebook(String email, String token) {
        return RestClient.get()
                .loginWithFacebook(email, token);
    }

    @Override
    public Observable<Response<User>> requestLoginWithGoogle(String email, String token) {
        return RestClient.get()
                .loginWithGoogle(email, token);
    }
}