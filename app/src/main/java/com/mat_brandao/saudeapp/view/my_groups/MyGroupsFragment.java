package com.mat_brandao.saudeapp.view.my_groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseFragment;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyGroupsFragment extends BaseFragment implements MyGroupsView {

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.groups_recycler)
    RecyclerView groupsRecycler;
    @Bind(R.id.empty_text_view)
    TextView emptyTextView;
    @Bind(R.id.progress_layout)
    FrameLayout progressLayout;

    private MyGroupsPresenterImpl mPresenter;

    public static MyGroupsFragment newInstance() {
        Bundle args = new Bundle();
        MyGroupsFragment fragment = new MyGroupsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_groups, container, false);
        ButterKnife.bind(this, view);

        groupsRecycler.setHasFixedSize(true);
        groupsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mPresenter = new MyGroupsPresenterImpl(this, getContext());
        return view;
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
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(getContext(), message);
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
    public void setGroupsAdapter(RecyclerView.Adapter adapter) {
        groupsRecycler.setAdapter(adapter);
    }

    @Override
    public void setEmptyViewVisibility(int visibility) {
        emptyTextView.setVisibility(visibility);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressLayout.setVisibility(visibility);
    }
}