
package com.mat_brandao.saudeapp.domain.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Postagem {

    @SerializedName("codPostagem")
    @Expose
    private Long codPostagem;
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
