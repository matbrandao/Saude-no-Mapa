package com.mat_brandao.saudeapp.view.group;

import com.mat_brandao.saudeapp.domain.model.Grupo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface GroupInteractor {
    Observable<Response<List<Grupo>>> requestGroup(String nomeFantasia);

    Observable<Response<ResponseBody>> requestCreateGroup(String nomeFantasia);
}