package com.mat_brandao.saudeapp.view.about;

import android.content.Context;
import android.text.Html;

import com.mat_brandao.saudeapp.R;

public class AboutPresenterImpl implements AboutPresenter {

    private AboutInteractorImpl mInteractor;
    private Context mContext;
    private AboutView mView;

    public AboutPresenterImpl(AboutView view, Context context) {
        mInteractor = new AboutInteractorImpl(context);
        mContext = context;
        mView = view;

        mView.setAboutText(Html.fromHtml(mContext.getString(R.string.about_text)));

        mView.setDoubsAndSuggestionsText(Html.fromHtml(mContext.getString(R.string.doubt_or_suggestions)));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onRetryClicked() {
        // TODO:
    }
}