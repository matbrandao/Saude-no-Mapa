package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface FavEstablishmentView extends BaseView.BaseProgressView {
    void setRecyclerAdapter(FavEstablishmentAdapter adapter);

    void setProgressLayoutVisibility(int visibility);

    void setEmptyViewVisibility(int visibility);
}