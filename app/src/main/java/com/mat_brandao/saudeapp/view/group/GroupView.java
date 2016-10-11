package com.mat_brandao.saudeapp.view.group;

import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericActionListener;
import com.mat_brandao.saudeapp.view.base.BaseView;

public interface GroupView extends BaseView.BaseProgressView {
    Establishment getIntentEstablishment();

    void setToolbarTitle(String title);

    void setGroupMembersAdapter(GroupMembersAdapter adapter);

    void showLeaveGroupDialog(GenericActionListener listener);

    void setEmptyTextVisibility(int visibility);

    void setIsRefreshing(boolean isRefreshing);
}