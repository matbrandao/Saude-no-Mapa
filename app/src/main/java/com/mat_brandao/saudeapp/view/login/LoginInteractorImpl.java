package com.mat_brandao.saudeapp.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Installation;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.domain.util.OnFormEmitted;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class LoginInteractorImpl implements LoginInteractor {
    private static final String FIRST_USE_KEY = "first_user_key";

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
    }

    @Override
    public Observable<Response<Installation>> requestCreateInstallation() {
        User user = mUserRepository.getUser();
        Installation installation = new Installation();
        installation.setAppId(Integer.valueOf(mContext.getString(R.string.app_id)));
        installation.setDateTime(DateUtil.getNowDate());
        installation.setDeviceOS(mContext.getString(R.string.device_os));
        installation.setDeviceToken(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
        installation.setUserId(user.getId());
        return RestClient.getHeader(user.getAppToken())
                .createInstallation(installation);
    }

    @Override
    public boolean isFirstUse() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isFirstUse = sharedPreferences.getBoolean(FIRST_USE_KEY, true);
        if (isFirstUse) {
            sharedPreferences.edit()
                    .putBoolean(FIRST_USE_KEY, false)
                    .apply();
        }
        // FIXME: 20/09/2016 
        return true;
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