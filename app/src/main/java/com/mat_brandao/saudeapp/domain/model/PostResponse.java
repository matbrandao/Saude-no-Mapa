package com.mat_brandao.saudeapp.domain.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResponse {

    @SerializedName("codPostagem")
    @Expose
    private Long codPostagem;
    @SerializedName("dataHoraPostagem")
    @Expose
    private String dataHoraPostagem;
    @SerializedName("codAutor")
    @Expose
    private Long codAutor;
    @SerializedName("conteudos")
    @Expose
    private List<Conteudo> conteudos = new ArrayList<>();
    @SerializedName("codObjetoDestino")
    @Expose
    private Long codObjetoDestino;
    @SerializedName("codTipoPostagem")
    @Expose
    private Long codTipoPostagem;
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    /**
     * 
     * @return
     *     The codPostagem
     */
    public Long getCodPostagem() {
        return codPostagem;
    }

    /**
     * 
     * @param codPostagem
     *     The codPostagem
     */
    public void setCodPostagem(Long codPostagem) {
        this.codPostagem = codPostagem;
    }

    /**
     * 
     * @return
     *     The dataHoraPostagem
     */
    public String getDataHoraPostagem() {
        return dataHoraPostagem;
    }

    /**
     * 
     * @param dataHoraPostagem
     *     The dataHoraPostagem
     */
    public void setDataHoraPostagem(String dataHoraPostagem) {
        this.dataHoraPostagem = dataHoraPostagem;
    }

    /**
     * 
     * @return
     *     The codAutor
     */
    public Long getCodAutor() {
        return codAutor;
    }

    /**
     * 
     * @param codAutor
     *     The codAutor
     */
    public void setCodAutor(Long codAutor) {
        this.codAutor = codAutor;
    }

    /**
     * 
     * @return
     *     The conteudos
     */
    public List<Conteudo> getConteudos() {
        return conteudos;
    }

    /**
     * 
     * @param conteudos
     *     The conteudos
     */
    public void setConteudos(List<Conteudo> conteudos) {
        this.conteudos = conteudos;
    }

    /**
     * 
     * @return
     *     The codObjetoDestino
     */
    public Long getCodObjetoDestino() {
        return codObjetoDestino;
    }

    /**
     * 
     * @param codObjetoDestino
     *     The codObjetoDestino
     */
    public void setCodObjetoDestino(Long codObjetoDestino) {
        this.codObjetoDestino = codObjetoDestino;
    }

    /**
     * 
     * @return
     *     The codTipoPostagem
     */
    public Long getCodTipoPostagem() {
        return codTipoPostagem;
    }

    /**
     * 
     * @param codTipoPostagem
     *     The codTipoPostagem
     */
    public void setCodTipoPostagem(Long codTipoPostagem) {
        this.codTipoPostagem = codTipoPostagem;
    }

    /**
     * 
     * @return
     *     The links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
