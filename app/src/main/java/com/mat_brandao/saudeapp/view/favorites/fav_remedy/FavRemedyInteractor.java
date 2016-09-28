package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Remedy;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface FavRemedyInteractor {
    Observable<Response<List<PostResponse>>> requestGetUserPosts();

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    void saveUserLikePostCode(Long likePostCode);

    Observable<Response<List<Remedy>>> requestGetRemedy(Long remedyCode);
}