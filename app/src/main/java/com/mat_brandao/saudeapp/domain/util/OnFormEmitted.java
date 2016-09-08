package com.mat_brandao.saudeapp.domain.util;

/**
 * Created by Mateus Brandão on 2/1/2016.
 */
public interface OnFormEmitted {
    void buttonChanged(Boolean isEnabled);

    void emailOnNext(Boolean isValid);

    void passwordOnNext(Boolean isValid);

    void rePasswordOnNext(Boolean isValid);

    void nameOnNext(Boolean isValid);
}
