package com.mat_brandao.saudeapp.view.main;

import com.mat_brandao.saudeapp.domain.model.User;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface MainInteractor {
    User getUser();

    Observable<Response<ResponseBody>> requestUserImage();

    String getProfilePhotoUrl();
}