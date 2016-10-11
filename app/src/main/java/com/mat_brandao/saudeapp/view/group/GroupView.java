package com.mat_brandao.saudeapp.view.group;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.view.base.BaseView;

public interface GroupView extends BaseView.BaseProgressView {
    Establishment getIntentEstablishment();

    void setToolbarTitle(String title);

    void setGroupMembersAdapter(GroupMembersAdapter adapter);
}