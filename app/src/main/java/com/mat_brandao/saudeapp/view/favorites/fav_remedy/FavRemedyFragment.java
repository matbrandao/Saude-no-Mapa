package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseFragment;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavRemedyFragment extends BaseFragment implements FavRemedyView {

    @Bind(R.id.fav_remedy_recycler)
    RecyclerView favRemedyRecycler;
    @Bind(R.id.empty_text_view)
    TextView emptyTextView;
    @Bind(R.id.progress_layout)
    LinearLayout progressLayout;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private FavRemedyPresenterImpl mPresenter;

    public static FavRemedyFragment newInstance() {
        Bundle args = new Bundle();
        FavRemedyFragment fragment = new FavRemedyFragment();
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
        View view = inflater.inflate(R.layout.fragment_fav_remedy, container, false);
        ButterKnife.bind(this, view);

        favRemedyRecycler.setHasFixedSize(true);
        favRemedyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mPresenter = new FavRemedyPresenterImpl(this, getContext());
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
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, v -> {
            mPresenter.onRetryClicked();
        });
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
    public void setRecyclerAdapter(FavRemedyAdapter adapter) {
        favRemedyRecycler.setAdapter(adapter);
    }

    @Override
    public void setProgressLayoutVisibility(int visibility) {
        progressLayout.setVisibility(visibility);
    }

    @Override
    public void setEmptyViewVisibility(int visibility) {
        emptyTextView.setVisibility(visibility);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}