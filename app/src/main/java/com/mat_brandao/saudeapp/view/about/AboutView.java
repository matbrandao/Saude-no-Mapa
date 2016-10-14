package com.mat_brandao.saudeapp.view.about;

import android.text.Spanned;

import com.mat_brandao.saudeapp.view.base.BaseView;

public interface AboutView extends BaseView.BaseProgressView {
    void setAboutText(Spanned text);

    void setDoubsAndSuggestionsText(Spanned text);
}