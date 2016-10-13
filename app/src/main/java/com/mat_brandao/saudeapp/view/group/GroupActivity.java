package com.mat_brandao.saudeapp.view.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.util.GenericActionListener;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;
import com.mat_brandao.saudeapp.view.establishment.EstablishmentPresenterImpl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupActivity extends BaseActivity implements GroupView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.group_members_recycler)
    RecyclerView groupMembersRecycler;
    @Bind(R.id.empty_text_view)
    TextView emptyTextView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.join_fab)
    FloatingActionButton joinFab;
    @Bind(R.id.chat_fab)
    FloatingActionButton chatFab;

    private GroupPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            finishActivity();
        });

        groupMembersRecycler.setHasFixedSize(true);
        groupMembersRecycler.setLayoutManager(new LinearLayoutManager(this));

        mPresenter = new GroupPresenterImpl(this, this);

        refreshLayout.setOnRefreshListener(mPresenter);
    }

    @OnClick(R.id.join_fab)
    void onJoinFabClick() {
        mPresenter.onJoinFabClick();
    }

    @OnClick(R.id.chat_fab)
    void onChatFabClick() {
        mPresenter.onChatFabClick();
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
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, v -> {
            mPresenter.onRetryClicked();
        });
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
    public Establishment getIntentEstablishment() {
        return (Establishment) getIntent().getSerializableExtra(EstablishmentPresenterImpl.ESTABLISHMENT_INTENT_KEY);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void setGroupMembersAdapter(GroupMembersAdapter adapter) {
        groupMembersRecycler.setAdapter(adapter);
    }

    @Override
    public void showLeaveGroupDialog(GenericActionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_leave_group_title);
        builder.setMessage(R.string.dialog_leave_group_message);
        builder.setPositiveButton(R.string.dialog_leave_group_positive, (dialog, which) -> {
            listener.onAction();
        });
        builder.setNegativeButton(R.string.dialog_leave_group_negative, null);
        builder.create().show();
    }

    @Override
    public void showJoinGroupDialog(GenericActionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_join_group_title);
        builder.setMessage(R.string.dialog_join_group_message);
        builder.setPositiveButton(R.string.dialog_join_group_positive, (dialog, which) -> {
            listener.onAction();
        });
        builder.setNegativeButton(R.string.dialog_join_group_negative, null);
        builder.create().show();
    }

    @Override
    public void setEmptyTextVisibility(int visibility) {
        emptyTextView.setVisibility(visibility);
    }

    @Override
    public void setIsRefreshing(boolean isRefreshing) {
        refreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void setJoinFabVisibility(int visibility) {
        joinFab.setVisibility(visibility);
    }

    @Override
    public void setChatFabVisibility(int visibility) {
        chatFab.setVisibility(visibility);
    }
}