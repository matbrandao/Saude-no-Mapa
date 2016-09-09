package com.mat_brandao.saudeapp.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.OnFormEmitted;
import com.mat_brandao.saudeapp.view.register.RegisterActivity;

import org.json.JSONException;

import java.util.Arrays;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LoginPresenterImpl implements LoginPresenter, OnFormEmitted {
    private static final String TAG = "LoginPresenterImpl";

    private LoginInteractorImpl mInteractor;
    private Context mContext;
    private LoginView mView;

    private boolean isFacebook;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    public LoginPresenterImpl(LoginView view, Context context) {
        mInteractor = new LoginInteractorImpl(context);
        mContext = context;
        mView = view;

        mView.toggleLoginButton(false);
        EditText emailEdit = mView.getEmailEditText();
        EditText passEdit = mView.getPasswordEditText();
        mInteractor.validateForms(emailEdit, passEdit, this);
    }

    @Override
    public void forgotPasswordClicked() {
        mView.showPasswordDialog(email -> {
            mView.showProgressDialog(mContext.getString(R.string.progress_wait));
            mSubscription.add(mInteractor.requestRememberPassword(email)
                    .onErrorReturn(throwable -> null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseResponse -> {
                        mView.dismissProgressDialog();
                        if (baseResponse == null) {
                            mView.showToast(mContext.getString(R.string.http_error_500));
                        } else {
                            mView.showToast(mContext.getString(R.string.remember_password_email_sent));
                        }
                    }));
        });
    }

    @Override
    public void onLoginWithAccountTextClicked() {
        mView.fadeInLoginLayout();
        mView.hideAccountLoginText();
    }

    @Override
    public void facebookLoginClicked() {
        mView.showProgressDialog("Efetuando login...");
        mInteractor.requestLoginToFacebook(Arrays.asList("email", "public_profile"), facebookCallback);
    }

    @Override
    public void normalLoginClicked(String email, String password) {
        mView.hideKeyboard();
        isFacebook = false;
        mView.showProgressDialog("Efetuando login");
        mSubscription.add(mInteractor
                .requestLoginWithAccount(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginObserver));
    }

    @Override
    public void registerButtonClicked() {
        mView.goToActivity(RegisterActivity.class);
    }

    @Override
    public void buttonChanged(Boolean isEnabled) {
        mView.toggleLoginButton(isEnabled);
    }

    @Override
    public void emailOnNext(Boolean isValid) {
        mView.toggleEmailError(isValid);
    }

    @Override
    public void passwordOnNext(Boolean isValid) {
        mView.togglePasswordError(isValid);
    }

    @Override
    public void rePasswordOnNext(Boolean isValid) {
        //  NOT IMPLEMENTED
    }

    @Override
    public void nameOnNext(Boolean isValid) {
        //  NOT IMPLEMENTED
    }

    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    (object, response) -> {
                        Log.d(TAG, "onSuccess: " + object);
                        Log.d(TAG, "onSuccess: " + response);
                        String email = "";
                        String name = "";
                        try {
                            name = object.getString("name");
                            email = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(email)) {
                            // TODO: 28-Jul-16
                        } else {
                            User user = new User();
                            user.setName(name);
                            // FIXME: 08-Sep-16
//                            Log.d(TAG, "onSuccess: " + loginResult.getAccessToken().getToken());
                            user.setEmail("x" + email);
                            user.setPassword(AccessToken.getCurrentAccessToken().getApplicationId() +
                                    AccessToken.getCurrentAccessToken().getUserId() + "x");
                            user.setPasswordType(User.FACEBOOK_LOGIN_TYPE);
                            mInteractor.saveUserToRealm(user);
                            isFacebook = true;
                            mSubscription.add(mInteractor.requestLoginWithFacebook(user.getEmail(), user.getPassword())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(loginObserver));
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();

            Log.d(TAG, "onSuccess() called with: " + "loginResult = [" + loginResult + "]");
        }

        @Override
        public void onCancel() {
            mView.dismissProgressDialog();
            mView.showToast(mContext.getString(R.string.login_error_try_again));
        }

        @Override
        public void onError(FacebookException error) {
            mView.dismissProgressDialog();
            mView.showToast(mContext.getString(R.string.login_error_try_again));
        }
    };

    Observer<Response<User>> loginObserver = new Observer<Response<User>>() {
        @Override
        public void onCompleted() {
            mView.dismissProgressDialog();
            Log.d(TAG, "onCompleted() called with: " + "");
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<User> userResponse) {
            if (!userResponse.isSuccessful()) {
                if (userResponse.code() == 401) {
                    if (isFacebook) {
                        mView.goToActivity(RegisterActivity.class);
                    } else {
                        try {
                            Error401 error401 = new Gson().fromJson(userResponse.errorBody().string(), Error401.class);
                            mView.showToast(error401.getMessageList().get(0).getText() + ".\nVerifique seus dados");
                        } catch (Exception e) {
                            mView.showToast(mContext.getString(R.string.http_error_generic));
                        }
                    }
                } else {
                    mView.showToast(mContext.getString(R.string.http_error_500));
                }
            } else {
                Timber.i("header = " + userResponse.headers().get("appToken"));
                Timber.i("name = " + userResponse.body().getName());
//                mInteractor.saveUserToRealm(userResponse.getData());
//                mView.goToActivity(MainActivity.class);
//                mView.finishActivity();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mInteractor.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRetryClicked() {
        // TODO: 07-Sep-16
    }
}