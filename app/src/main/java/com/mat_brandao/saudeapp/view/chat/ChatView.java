package com.mat_brandao.saudeapp.view.chat;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.mat_brandao.saudeapp.domain.model.FriendlyMessage;
import com.mat_brandao.saudeapp.view.base.BaseView;

public interface ChatView extends BaseView.BaseProgressView {
    void setMessageAdapter(FirebaseRecyclerAdapter<FriendlyMessage, ChatPresenterImpl.MessageViewHolder> adapter);

    void scrollRecyclerToPosition(int positionStart);

    int getLastPositionVisible();

    void setProgressBarVisibility(int visibility);
}