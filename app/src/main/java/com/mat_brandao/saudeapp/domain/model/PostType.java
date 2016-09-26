package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mateus Brandão on 26/09/2016.
 */

public class PostType {
    @Expose
    private long codTipoPostagem;

    public PostType(long codTipoPostagem) {
        this.codTipoPostagem = codTipoPostagem;
    }

    public long getCodTipoPostagem() {
        return codTipoPostagem;
    }

    public void setCodTipoPostagem(long codTipoPostagem) {
        this.codTipoPostagem = codTipoPostagem;
    }
}
