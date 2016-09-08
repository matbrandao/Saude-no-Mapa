package com.mat_brandao.saudeapp.view.register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class RegisterActivity extends BaseActivity implements RegisterView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @Bind(R.id.name_text_input_layout)
    TextInputLayout nameTextInputLayout;
    @Bind(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @Bind(R.id.email_text_input_layout)
    TextInputLayout emailTextInputLayout;
    @Bind(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @Bind(R.id.password_text_input_layout)
    TextInputLayout passwordTextInputLayout;
    @Bind(R.id.confirm_password_edit_text)
    TextInputEditText confirmPasswordEditText;
    @Bind(R.id.confirm_password_text_input_layout)
    TextInputLayout confirmPasswordTextInputLayout;
    @Bind(R.id.save_user_fab)
    FloatingActionButton saveUserFab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.birth_date_edit_text)
    TextInputEditText birthDateEditText;
    @Bind(R.id.birth_date_input_layout)
    TextInputLayout birthDateInputLayout;
    @Bind(R.id.cep_edit_text)
    TextInputEditText cepEditText;
    @Bind(R.id.cep_input_layout)
    TextInputLayout cepInputLayout;
    @Bind(R.id.sex_spinner)
    Spinner sexSpinner;

    private static RegisterPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finishActivity());

        toggleFabButton(false);
        cepEditText.addTextChangedListener(MaskUtil.insertCep(cepEditText));
//        birthDateEditText.addTextChangedListener(MaskUtil.insertDate(birthDateEditText));

        birthDateEditText.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mPresenter.onBirthDateTouchListener();
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        });

        mPresenter = new RegisterPresenterImpl(this, this);
    }

    @OnClick(R.id.save_user_fab)
    void onSaveUserFabClick() {
        mPresenter.onSaveFabClick();
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
        super.showConnectionError(coordinatorLayout, view -> {
            mPresenter.onRetryClicked();
        });
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public Observable<CharSequence> registerNameObservable() {
        return RxTextView.textChanges(nameEditText);
    }

    @Override
    public Observable<CharSequence> registerBirthDateObservable() {
        return RxTextView.textChanges(birthDateEditText);
    }

    @Override
    public Observable<CharSequence> registerEmailObservable() {
        return RxTextView.textChanges(emailEditText);
    }

    @Override
    public Observable<CharSequence> registerCepObservable() {
        return RxTextView.textChanges(cepEditText);
    }

    @Override
    public Observable<CharSequence> registerPasswordObservable() {
        return RxTextView.textChanges(passwordEditText);
    }

    @Override
    public Observable<CharSequence> registerRePasswordObservable() {
        return RxTextView.textChanges(confirmPasswordEditText);
    }

    @Override
    public Observable<Integer> registerSexSpinnerObservable() {
        return RxAdapterView.itemSelections(sexSpinner);
    }

    @Override
    public void toggleNameError(boolean isValid) {
        if (isValid) {
            nameTextInputLayout.setError(null);
        } else {
            nameTextInputLayout.setError(getString(R.string.name_too_short));
        }
        nameTextInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void toggleBirthDateError(boolean isValid) {
        if (isValid) {
            birthDateInputLayout.setError(null);
        } else {
            birthDateInputLayout.setError(getString(R.string.invalid_date));
        }
        birthDateInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void toggleEmailError(boolean isValid) {
        if (isValid) {
            emailTextInputLayout.setError(null);
        } else {
            emailTextInputLayout.setError(getString(R.string.invalid_email));
        }
        emailTextInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void toggleCepError(boolean isValid) {
        if (isValid) {
            cepInputLayout.setError(null);
        } else {
            cepInputLayout.setError(getString(R.string.invalid_cep));
        }
        cepInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void togglePasswordError(boolean isValid) {
        if (isValid) {
            passwordTextInputLayout.setError(null);
        } else {
            passwordTextInputLayout.setError(getString(R.string.invalid_password));
        }
        passwordTextInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void toggleRePasswordError(boolean isValid) {
        if (isValid) {
            confirmPasswordTextInputLayout.setError(null);
        } else {
            confirmPasswordTextInputLayout.setError(getString(R.string.invalid_re_password));
        }
        confirmPasswordTextInputLayout.setErrorEnabled(!isValid);
    }

    @Override
    public void toggleFabButton(boolean enabled) {
        if (enabled) {
            saveUserFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
            saveUserFab.setEnabled(true);
        } else {
            saveUserFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.login_edit_text_color)));
            saveUserFab.setEnabled(false);
        }
    }

    @Override
    public void setNameText(String name) {
        nameEditText.setText(name);
    }

    @Override
    public void setEmailText(String email) {
        emailEditText.setText(email);
    }

    @Override
    public void setPasswordText(String password) {
        passwordEditText.setText(password);
    }

    @Override
    public void setPasswordConfirmationText(String passwordConfirmation) {
        confirmPasswordEditText.setText(passwordConfirmation);
    }

    @Override
    public void disableFields() {
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        confirmPasswordEditText.setEnabled(false);
    }

    @Override
    public void showDateDialog() {
        DialogFragment newFragment = new SelectDate();
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public void setSexSpinnerAdapter(ArrayAdapter<String> stringArrayAdapter) {
        sexSpinner.setAdapter(stringArrayAdapter);
    }

    @Override
    public void setBirthDateText(String date) {
        birthDateEditText.setText(date);
    }

    @SuppressLint("ValidFragment")
    public static class SelectDate extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public SelectDate() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), this, yy, mm, dd);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            return mDatePicker;
        }

        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            mPresenter.onDateSet(yy, mm, dd);
        }
    }
}