package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface FavEstablishmentInteractor {
    Observable<Response<List<PostResponse>>> requestGetUserPosts();

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    void saveUserLikePostCode(Long likePostCode);

    Observable<Response<List<Establishment>>> requestGetEstablishment(Long establishmentCode);
}