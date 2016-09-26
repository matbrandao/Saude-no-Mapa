
package com.mat_brandao.saudeapp.domain.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conteudo {

    @SerializedName("codConteudoPostagem")
    @Expose
    private Long codConteudoPostagem;
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    /**
     * 
     * @return
     *     The codConteudoPostagem
     */
    public Long getCodConteudoPostagem() {
        return codConteudoPostagem;
    }

    /**
     * 
     * @param codConteudoPostagem
     *     The codConteudoPostagem
     */
    public void setCodConteudoPostagem(Long codConteudoPostagem) {
        this.codConteudoPostagem = codConteudoPostagem;
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
