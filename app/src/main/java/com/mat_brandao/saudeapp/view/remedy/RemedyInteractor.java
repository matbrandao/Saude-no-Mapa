package com.mat_brandao.saudeapp.view.remedy;

import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.Remedy;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface RemedyInteractor {
    Observable<Response<List<Remedy>>> requestRemediesByName(String name);

    Observable<Response<List<Remedy>>> requestRemediesByBarCode(String barcode);

    Observable<Response<ResponseBody>> requestCreateLikePost();

    Observable<Response<List<PostResponse>>> requestGetUserPosts();

    Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem);

    Observable<Response<ResponseBody>> requestLikeRemedy(Long postCode, Long codRemedy);

    Observable<Response<ResponseBody>> requestLikeRemedy(Long codRemedy);

    Observable<Response<ResponseBody>> requestDisLikeRemedy(Long codRemedy);

    boolean isRemedyLiked(Long codRemedy);

    void saveUserLikePostCode(Long likePostCode);

    void addRemedyToLikedList(Long contentCode, Long remedyCode);

    boolean hasLikePostCode();

    String getPostCode();
}