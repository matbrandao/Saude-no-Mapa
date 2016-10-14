package com.mat_brandao.saudeapp.view.my_groups;

import com.mat_brandao.saudeapp.domain.model.Grupo;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface MyGroupsInteractor {
    Observable<Response<List<Grupo>>> requestMyGroups();

    int getChatItemCountByGroupId(Integer groupId);

    void saveItemCount(Integer codGrupo, int itemCount);
}