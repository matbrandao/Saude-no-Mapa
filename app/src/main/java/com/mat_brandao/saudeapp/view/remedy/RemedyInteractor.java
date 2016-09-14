package com.mat_brandao.saudeapp.view.remedy;

import com.mat_brandao.saudeapp.domain.model.Remedy;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface RemedyInteractor {
    Observable<Response<List<Remedy>>> requestRemediesByName(String name);

    Observable<Response<List<Remedy>>> requestRemediesByBarCode(String barcode);
}