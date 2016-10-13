package com.mat_brandao.saudeapp.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.FriendlyMessage;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;
    @Bind(R.id.messageEditText)
    EditText messageEditText;
    @Bind(R.id.sendButton)
    ImageView sendButton;
    @Bind(R.id.chat_entry)
    RelativeLayout chatEntry;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ChatPresenterImpl mPresenter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            finishActivity();
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        messageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mPresenter = new ChatPresenterImpl(this, this);
    }

    @Override
    public void showToast(String text) {
        super.showToast(text);
    }

    @Override
    public void goToActivity(Class<?> activity) {
        super.goToActivity(activity);
    }

    @Override
    public void goToActivity(Intent intent) {
        super.goToActivity(intent);
    }

    @Override
    public void finishActivity() {
        supportFinishAfterTransition();
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, v -> {
            mPresenter.onRetryClicked();
        });
    }

    @Override
    public void setMessageAdapter(FirebaseRecyclerAdapter<FriendlyMessage, ChatPresenterImpl.MessageViewHolder> adapter) {
        messageRecyclerView.setAdapter(adapter);
    }

    @Override
    public void scrollRecyclerToPosition(int positionStart) {
        messageRecyclerView.scrollToPosition(positionStart);
    }

    @Override
    public int getLastPositionVisible() {
        return mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }
}