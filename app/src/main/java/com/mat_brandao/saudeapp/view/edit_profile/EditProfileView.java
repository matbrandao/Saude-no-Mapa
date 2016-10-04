package com.mat_brandao.saudeapp.view.edit_profile;

import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.view.base.BaseView;

import rx.Observable;

public interface EditProfileView extends BaseView.BaseProgressView {
    Observable<CharSequence> registerNameObservable();

    Observable<CharSequence> registerBirthDateObservable();

    Observable<CharSequence> registerEmailObservable();

    Observable<CharSequence> registerCepObservable();

    Observable<CharSequence> registerBioObservable();

    Observable<Integer> registerSexSpinnerObservable();

    void toggleNameError(boolean isValid);

    void toggleBirthDateError(boolean isValid);

    void toggleEmailError(boolean isValid);

    void toggleCepError(boolean isValid);

    void toggleFabButton(boolean enabled);

    void setNameText(String name);

    void setEmailText(String email);

    void disableFields();

    void showDateDialog();

    void setSexSpinnerAdapter(ArrayAdapter<String> stringArrayAdapter);

    void setBirthDateText(String date);

    void loadImageToAvatar(Integer url);

    void loadImageToAvatar(String profilePhotoUrl);

    void setSexSelecion(int selection);

    void setCepText(String cep);

    void setBioText(String bio);
}