package com.mat_brandao.saudeapp.view.login;

import android.content.Intent;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mat_brandao.saudeapp.domain.util.StringListener;
import com.mat_brandao.saudeapp.view.base.BaseView;

public interface LoginView extends BaseView.BaseProgressView {
    void fadeInLoginLayout();

    void hideAccountLoginText();

    void toggleLoginButton(boolean enabled);

    EditText getEmailEditText();

    EditText getPasswordEditText();

    void toggleEmailError(Boolean isValid);

    void togglePasswordError(Boolean isValid);

    void hideKeyboard();

    void showPasswordDialog(StringListener listener);

    void setGoogleButtonScope(GoogleSignInOptions gso);

    void startActivityForResult(Intent signInIntent);
}