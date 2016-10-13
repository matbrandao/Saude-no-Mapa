package com.mat_brandao.saudeapp.view.my_groups;

import android.support.v7.widget.RecyclerView;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface MyGroupsView extends BaseView.BaseProgressView {
    void setGroupsAdapter(RecyclerView.Adapter adapter);

    void setEmptyViewVisibility(int visibility);

    void setProgressBarVisibility(int visibility);
}