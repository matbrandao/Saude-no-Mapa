package com.mat_brandao.saudeapp.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.StringListener;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView {

    @Bind(R.id.logo_image)
    ImageView logoImage;
    @Bind(R.id.login_email_edit)
    EditText loginEmailEdit;
    @Bind(R.id.login_email_input)
    TextInputLayout loginEmailInput;
    @Bind(R.id.login_password_edit)
    EditText loginPasswordEdit;
    @Bind(R.id.login_password_input)
    TextInputLayout loginPasswordInput;
    @Bind(R.id.login_normal_button)
    Button loginNormalButton;
    @Bind(R.id.account_register)
    TextView accountRegister;
    @Bind(R.id.account_layout)
    LinearLayout accountLayout;
    @Bind(R.id.login_facebook_button)
    Button loginFacebookButton;
    @Bind(R.id.account_login)
    TextView accountLogin;

    private LoginPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter = new LoginPresenterImpl(this, this);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && loginFacebookButton instanceof AppCompatButton) {
            ((AppCompatButton) loginFacebookButton).setSupportBackgroundTintList(getResources().getColorStateList(R.color.facebook_blue));
        } else {
            ViewCompat.setBackgroundTintList(loginFacebookButton, getResources().getColorStateList(R.color.facebook_blue));
        }
    }

    @Override
    public void showToast(String text) {
        super.showToast(text);
    }

    @Override
    public void goToActivity(Class<?> activity) {
        super.goToActivity(activity);
    }

    @Override
    public void goToActivity(Intent intent) {
        super.goToActivity(intent);
    }

    @Override
    public void finishActivity() {
        supportFinishAfterTransition();
    }

    @Override
    public void showNoConnectionSnackBar() {
// TODO: 07-Sep-16
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @OnClick(R.id.forgot_password)
    void forgotPassword() {
        mPresenter.forgotPasswordClicked();
    }

    @OnClick(R.id.account_register)
    void accountRegister() {
        mPresenter.registerButtonClicked();
    }

    @OnClick(R.id.login_facebook_button)
    void facebookLogin() {
        mPresenter.facebookLoginClicked();
    }

    @OnClick(R.id.login_normal_button)
    void normalLogin() {
        mPresenter.normalLoginClicked(loginEmailEdit.getText().toString(), loginPasswordEdit.getText().toString());
    }

    @OnClick(R.id.account_login)
    void accountLoginTextClicked() {
        mPresenter.onLoginWithAccountTextClicked();
    }

    @Override
    public void fadeInLoginLayout() {
        accountLayout.setVisibility(View.VISIBLE);
        accountLayout.animate().alpha(1).setDuration(500);
    }

    @Override
    public void hideAccountLoginText() {
        accountLogin.animate().alpha(0).setDuration(500).withEndAction(() -> accountLogin.setEnabled(false));
    }

    @Override
    public void toggleLoginButton(boolean enabled) {
        loginNormalButton.setEnabled(enabled);
    }

    @Override
    public EditText getEmailEditText() {
        return loginEmailEdit;
    }

    @Override
    public EditText getPasswordEditText() {
        return loginPasswordEdit;
    }

    @Override
    public void toggleEmailError(Boolean isValid) {
        loginEmailInput.setError(getResources().getString(R.string.invalid_email));
        loginEmailInput.setErrorEnabled(!isValid);
    }

    @Override
    public void togglePasswordError(Boolean isValid) {
        loginPasswordInput.setError(getResources().getString(R.string.invalid_password));
        loginPasswordInput.setErrorEnabled(isValid);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(loginEmailEdit.getWindowToken(), 0);
    }

    @Override
    public void showPasswordDialog(StringListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password_layout, null);
        EditText editText = (EditText) view.findViewById(R.id.email_edit_text);
        builder.setView(view);

        builder.setTitle("Recuperar Senha");
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancelar", null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveButton.setOnClickListener(v -> {
                String email = editText.getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    listener.onNext(email);
                    dialogInterface.dismiss();
                } else {
                    showToast(getString(R.string.invalid_email));
                }
            });
            negativeButton.setOnClickListener(v -> dialogInterface.dismiss());
        });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}