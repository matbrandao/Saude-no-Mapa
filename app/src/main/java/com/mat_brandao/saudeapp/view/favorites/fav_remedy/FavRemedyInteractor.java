package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Remedy;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface FavRemedyInteractor {
    Observable<Response<List<PostResponse>>> requestGetUserPosts();

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    Observable<Response<List<Remedy>>> requestGetRemedy(Long remedyCode);

    Observable<Response<ResponseBody>> requestLikeRemedy(Long postCode, Long codRemedy);

    Observable<Response<ResponseBody>> requestLikeRemedy(Long codRemedy);

    Observable<Response<ResponseBody>> requestDisLikeRemedy(Long codRemedy);

    void addRemedyToLikedList(Long contentCode, Long remedyCode);

    void saveUserLikePostCode(Long likePostCode);

    void removeDislikedContentCode();

    void clearLikedRemedies();

    String getPostCode();

    int getLikedRemedyCount();
}