package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mateus Brandão on 10/10/2016.
 */

public class CreateGroup {
    @Expose
    private Integer codAplicativo;
    @Expose
    private String descricao;

    public CreateGroup(Integer codAplicativo, String descricao) {
        this.codAplicativo = codAplicativo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodAplicativo() {
        return codAplicativo;
    }

    public void setCodAplicativo(Integer codAplicativo) {
        this.codAplicativo = codAplicativo;
    }
}
