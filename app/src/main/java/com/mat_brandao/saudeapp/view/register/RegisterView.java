package com.mat_brandao.saudeapp.view.register;

import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.view.base.BaseView;

import rx.Observable;

public interface RegisterView extends BaseView.BaseProgressView {
    Observable<CharSequence> registerNameObservable();

    Observable<CharSequence> registerBirthDateObservable();

    Observable<CharSequence> registerEmailObservable();

    Observable<CharSequence> registerCepObservable();

    Observable<CharSequence> registerPasswordObservable();

    Observable<CharSequence> registerRePasswordObservable();

    Observable<Integer> registerSexSpinnerObservable();

    void toggleNameError(boolean isValid);

    void toggleBirthDateError(boolean isValid);

    void toggleEmailError(boolean isValid);

    void toggleCepError(boolean isValid);

    void togglePasswordError(boolean isValid);

    void toggleRePasswordError(boolean isValid);

    void toggleFabButton(boolean enabled);

    void setNameText(String name);

    void setEmailText(String email);

    void setPasswordText(String password);

    void setPasswordConfirmationText(String passwordConfirmation);

    void disableFields();

    void showDateDialog();

    void setSexSpinnerAdapter(ArrayAdapter<String> stringArrayAdapter);

    void setBirthDateText(String date);

    void loadImageToAvatar(Integer url);
}