package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus Brandão on 11/10/2016.
 */

public class MembroGrupo implements Serializable {
    @SerializedName("dataHoraAtivo")
    @Expose
    private String dataHoraAtivo;
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<>();
    private Long membroId;
    private Long usuarioId;

    /**
     *
     * @return
     * The dataHoraAtivo
     */
    public String getDataHoraAtivo() {
        return dataHoraAtivo;
    }

    /**
     *
     * @param dataHoraAtivo
     * The dataHoraAtivo
     */
    public void setDataHoraAtivo(String dataHoraAtivo) {
        this.dataHoraAtivo = dataHoraAtivo;
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

    public Long getMembroId() {
        return membroId;
    }

    public void setMembroId(Long membroId) {
        this.membroId = membroId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
