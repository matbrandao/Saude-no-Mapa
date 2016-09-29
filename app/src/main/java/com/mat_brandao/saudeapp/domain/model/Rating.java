package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Mateus Brandão on 29/09/2016.
 */

public class Rating implements Serializable {
    @Expose
    private Float media;
    @Expose
    private Integer contagem;

    public Float getMedia() {
        return media;
    }

    public void setMedia(Float media) {
        this.media = media;
    }

    public Integer getContagem() {
        return contagem;
    }

    public void setContagem(Integer contagem) {
        this.contagem = contagem;
    }
}
