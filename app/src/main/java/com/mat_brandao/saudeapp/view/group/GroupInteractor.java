package com.mat_brandao.saudeapp.view.group;

import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.model.MembroGrupo;
import com.mat_brandao.saudeapp.domain.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface GroupInteractor {
    Observable<Response<List<Grupo>>> requestGroup(String nomeFantasia);

    Observable<Response<ResponseBody>> requestCreateGroup(String nomeFantasia);

    Observable<Response<List<MembroGrupo>>> requestGroupMembers(int groupId);

    Observable<Response<User>> requestUser(Long userId);

    Observable<Response<ResponseBody>> requestLeaveGroup(Integer codGrupo, Long membroId);

    User getUser();
}