package com.mat_brandao.saudeapp.view.edit_profile;

import com.mat_brandao.saudeapp.domain.model.User;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface EditProfileInteractor {
    User getUser();

    String getProfilePhotoUrl();

    String parseDate(String birthDate);

    Observable<Response<ResponseBody>> requestUpdateNormalUser(String name, String email, String sex,
                                                               String bio, String cep, long birthDate);

    Observable<Response<ResponseBody>> requestUpdateFacebookUser(String name, String email, String sex,
                                                         String bio, String cep, long birthDate);

    Observable<Response<ResponseBody>> requestUpdateGoogleUser(String name, String email, String sex,
                                                               String bio, String cep, long birthDate);

    void updateRealmUser(String mName, String mEmail, String mSelectedSex,
                         String date, String mCep, String mBio);
}