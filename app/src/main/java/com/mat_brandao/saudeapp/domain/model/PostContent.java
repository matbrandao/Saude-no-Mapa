
package com.mat_brandao.saudeapp.domain.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostContent {

    @SerializedName("codConteudoPost")
    @Expose
    private Long codConteudoPost;
    @SerializedName("texto")
    @Expose
    private String texto;
    @SerializedName("postagem")
    @Expose
    private Postagem postagem;
    @SerializedName("valor")
    @Expose
    private Long valor;
    @SerializedName("links")
    @Expose
    private List<Link_> links = new ArrayList<Link_>();
    @SerializedName("JSON")
    @Expose
    private String jSON;

    /**
     * 
     * @return
     *     The codConteudoPost
     */
    public Long getCodConteudoPost() {
        return codConteudoPost;
    }

    /**
     * 
     * @param codConteudoPost
     *     The codConteudoPost
     */
    public void setCodConteudoPost(Long codConteudoPost) {
        this.codConteudoPost = codConteudoPost;
    }

    /**
     * 
     * @return
     *     The texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * 
     * @param texto
     *     The texto
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * 
     * @return
     *     The postagem
     */
    public Postagem getPostagem() {
        return postagem;
    }

    /**
     * 
     * @param postagem
     *     The postagem
     */
    public void setPostagem(Postagem postagem) {
        this.postagem = postagem;
    }

    /**
     * 
     * @return
     *     The valor
     */
    public Long getValor() {
        return valor;
    }

    /**
     * 
     * @param valor
     *     The valor
     */
    public void setValor(Long valor) {
        this.valor = valor;
    }

    /**
     * 
     * @return
     *     The links
     */
    public List<Link_> getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(List<Link_> links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The jSON
     */
    public String getJSON() {
        return jSON;
    }

    /**
     * 
     * @param jSON
     *     The JSON
     */
    public void setJSON(String jSON) {
        this.jSON = jSON;
    }

}
