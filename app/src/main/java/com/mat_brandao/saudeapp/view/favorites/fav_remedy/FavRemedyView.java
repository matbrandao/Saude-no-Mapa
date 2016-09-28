package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface FavRemedyView extends BaseView.BaseProgressView {
    void setRecyclerAdapter(FavRemedyAdapter adapter);

    void setProgressLayoutVisibility(int visibility);

    void setEmptyViewVisibility(int visibility);
}