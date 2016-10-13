package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus Brandão on 10/10/2016.
 */

public class Grupo implements Serializable {
    @SerializedName("descricao")
    @Expose
    private String descricao;
    @SerializedName("codGrupo")
    @Expose
    private Integer codGrupo;
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    /**
     *
     * @return
     * The descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     *
     * @param descricao
     * The descricao
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     *
     * @return
     * The codGrupo
     */
    public Integer getCodGrupo() {
        return codGrupo;
    }

    /**
     *
     * @param codGrupo
     * The codGrupo
     */
    public void setCodGrupo(Integer codGrupo) {
        this.codGrupo = codGrupo;
    }

    /**
     *
     * @return
     * The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
