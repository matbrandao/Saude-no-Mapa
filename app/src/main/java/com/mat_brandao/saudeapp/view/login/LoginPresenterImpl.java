package com.mat_brandao.saudeapp.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.OnFormEmitted;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.mat_brandao.saudeapp.view.register.RegisterActivity;

import org.json.JSONException;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenterImpl implements LoginPresenter, OnFormEmitted, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginPresenterImpl";

    private LoginInteractorImpl mInteractor;
    private Context mContext;
    private LoginView mView;

    private boolean isFacebook;

    private CompositeSubscription mSubscription = new CompositeSubscription();
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsValidationFromDialog;
    private ReactivateViews mBottomViews;
    private BottomSheetDialog mBottomSheetDialog;
    private GoogleSignInOptions mGso;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
    }


    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();
        mView = null;
    }

    @Override
    public void onRetryClicked() {
    }

    public LoginPresenterImpl(LoginView view, Context context) {
        mInteractor = new LoginInteractorImpl(context);
        mContext = context;
        mView = view;

        mView.toggleLoginButton(false);
        EditText emailEdit = mView.getEmailEditText();
        EditText passEdit = mView.getPasswordEditText();
        mInteractor.validateLoginForms(emailEdit, passEdit, this);

        configureGoogleClient();
    }

    private void configureGoogleClient() {
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((LoginActivity) mContext, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();

        mView.setGoogleButtonScope(mGso);
    }

    @Override
    public void onReactivateAccountClicked() {
        mIsValidationFromDialog = true;
        mBottomSheetDialog = new BottomSheetDialog(mContext);

        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_reactivate_account, null);
        mBottomViews = new ReactivateViews();
        ButterKnife.bind(mBottomViews, dialogView);

        mInteractor.validateReactivateForms(mBottomViews.loginEmailEdit, mBottomViews.loginPasswordEdit, this);

        mBottomViews.loginGoogleButton.setSize(SignInButton.SIZE_STANDARD);
        mBottomViews.loginGoogleButton.setScopes(mGso.getScopeArray());

        mBottomViews.filterButton.setOnClickListener(v -> {
            requestReactivateNormalAccount();
        });

        mBottomViews.loginGoogleButton.setOnClickListener(v -> {
            googleLoginClicked();
        });

        mBottomViews.loginFacebookButton.setOnClickListener(v -> {
            facebookLoginClicked();
        });

        mBottomSheetDialog.setContentView(dialogView);

        mBottomViews.bottomSheet.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(mBottomViews.bottomSheet.getMeasuredHeight() + 200);

        mBottomSheetDialog.setOnDismissListener(dialog -> {
            mIsValidationFromDialog = false;
        });

        mBottomSheetDialog.show();
    }

    private void requestReactivateNormalAccount() {
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        mSubscription.add(mInteractor.requestReactivateNormalAccount(mBottomViews.loginEmailEdit.getText().toString(),
                mBottomViews.loginPasswordEdit.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reactivateObserver));
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
    public void googleLoginClicked() {
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mView.startActivityForResult(signInIntent);
    }

    @Override
    public void facebookLoginClicked() {
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        mInteractor.requestLoginToFacebook(Arrays.asList("email", "public_profile"), facebookCallback);
    }

    @Override
    public void normalLoginClicked(String email, String password) {
        mView.hideKeyboard();
        isFacebook = false;
        mView.showProgressDialog(mContext.getString(R.string.progress_logging_in));
        mSubscription.add(mInteractor
                .requestLoginWithAccount(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginObserver));
    }

    @Override
    public void registerButtonClicked() {
        mInteractor.clearUsers();
        mView.goToActivity(RegisterActivity.class);
    }

    @Override
    public void buttonChanged(Boolean isEnabled) {
        if (mIsValidationFromDialog) {
            toggleReactivateButton(isEnabled);
        } else {
            mView.toggleLoginButton(isEnabled);
        }
    }

    @Override
    public void emailOnNext(Boolean isValid) {
        if (mIsValidationFromDialog) {
            toggleEmailError(isValid);
        } else {
            mView.toggleEmailError(isValid);
        }
    }

    @Override
    public void passwordOnNext(Boolean isValid) {
        if (mIsValidationFromDialog) {
            togglePasswordError(isValid);
        } else {
            mView.togglePasswordError(isValid);
        }
    }

    private void toggleReactivateButton(Boolean isEnabled) {
        mBottomViews.filterButton.setEnabled(isEnabled);
    }

    private void toggleEmailError(Boolean isValid) {
        mBottomViews.loginEmailInput.setError(mContext.getString(R.string.invalid_email));
        mBottomViews.loginEmailInput.setErrorEnabled(!isValid);
    }

    private void togglePasswordError(Boolean isValid) {
        mBottomViews.loginPasswordInput.setError(mContext.getString(R.string.invalid_password));
        mBottomViews.loginPasswordInput.setErrorEnabled(isValid);
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
                            if (mIsValidationFromDialog) {
                                mSubscription.add(mInteractor.requestReactivateFacebookAccount(AccessToken.getCurrentAccessToken().getApplicationId() +
                                        AccessToken.getCurrentAccessToken().getUserId())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(reactivateObserver));
                            } else {
                                User user = new User();
                                user.setName(name);
//                            Log.d(TAG, "onSuccess: " + loginResult.getAccessToken().getToken());
                                user.setEmail(email);
                                user.setPassword(AccessToken.getCurrentAccessToken().getApplicationId() +
                                        AccessToken.getCurrentAccessToken().getUserId());
                                user.setPasswordType(User.FACEBOOK_LOGIN_TYPE);
                                mInteractor.saveUserToRealm(user);
                                isFacebook = true;
                                mSubscription.add(mInteractor.requestLoginWithFacebook(user.getEmail(), user.getPassword())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(loginObserver));
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
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
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<User> userResponse) {
            if (!userResponse.isSuccessful()) {
                mView.dismissProgressDialog();
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
                User user = userResponse.body();
                user.setAppToken(userResponse.headers().get("appToken"));
                mInteractor.saveUserToRealm(user);

                if (mInteractor.isFirstUse()) {
                    mInteractor.requestCreateInstallation()
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(throwable -> null)
                            .subscribe(installationResponse -> {
                                mView.dismissProgressDialog();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mView.goToActivity(intent);
                            });
                } else {
                    mView.dismissProgressDialog();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mView.goToActivity(intent);
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            mInteractor.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            if (mIsValidationFromDialog) {
                GoogleSignInAccount acct = result.getSignInAccount();
                mSubscription.add(mInteractor.requestReactivateGoogleAccount(acct.zzahf())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(reactivateObserver));
            } else {
                GoogleSignInAccount acct = result.getSignInAccount();
                User user = new User();
                user.setName(acct.getDisplayName());
                user.setEmail(acct.getEmail());
                user.setPassword(acct.zzahf());
                user.setPasswordType(User.GOOGLE_LOGIN_TYPE);
                mInteractor.saveUserToRealm(user);
                isFacebook = true;
                mSubscription.add(mInteractor.requestLoginWithGoogle(user.getEmail(), user.getPassword())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(loginObserver));
            }
        } else {
            mView.showToast(mContext.getString(R.string.login_error_try_again));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mView.showToast(mContext.getString(R.string.http_error_generic));
    }

    private Observer<Response<ResponseBody>> reactivateObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showToast(mContext.getString(R.string.http_error_no_connection));
        }

        @Override
        public void onNext(Response<ResponseBody> responseBodyResponse) {
            mView.dismissProgressDialog();
            if (responseBodyResponse.isSuccessful()) {
                mBottomSheetDialog.dismiss();
                mView.showToast(mContext.getString(R.string.account_reactivated));
            } else {
                if (responseBodyResponse.code() == 404) {
                    mView.showToast(mContext.getString(R.string.user_not_found));
                } else {
                    mView.showToast(mContext.getString(R.string.http_error_500));
                }
            }
        }
    };

    public class ReactivateViews {
        @Bind(R.id.bottom_sheet)
        NestedScrollView bottomSheet;
        @Bind(R.id.login_email_edit)
        EditText loginEmailEdit;
        @Bind(R.id.login_email_input)
        TextInputLayout loginEmailInput;
        @Bind(R.id.login_password_edit)
        EditText loginPasswordEdit;
        @Bind(R.id.login_password_input)
        TextInputLayout loginPasswordInput;
        @Bind(R.id.filter_button)
        Button filterButton;
        @Bind(R.id.login_facebook_button)
        Button loginFacebookButton;
        @Bind(R.id.login_google_button)
        SignInButton loginGoogleButton;
    }
}