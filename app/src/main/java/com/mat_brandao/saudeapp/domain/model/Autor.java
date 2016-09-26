package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mateus Brandão on 26/09/2016.
 */

public class Autor {
    @Expose
    private long codPessoa;

    public Autor(long codPessoa) {
        this.codPessoa = codPessoa;
    }

    public long getCodPessoa() {
        return codPessoa;
    }

    public void setCodPessoa(long codPessoa) {
        this.codPessoa = codPessoa;
    }
}
