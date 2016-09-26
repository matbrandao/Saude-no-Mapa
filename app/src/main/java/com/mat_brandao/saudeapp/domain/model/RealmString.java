package com.mat_brandao.saudeapp.domain.model;

import io.realm.RealmObject;

/**
 * Created by Mateus Brandão on 26/09/2016.
 */
public class RealmString extends RealmObject {
    private String string;

    public RealmString() {
    }

    public RealmString(String establishment) {
        this.string = establishment;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
