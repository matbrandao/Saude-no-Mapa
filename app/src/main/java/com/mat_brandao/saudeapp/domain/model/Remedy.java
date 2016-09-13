package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mateus Brandão on 13/09/2016.
 */
public class Remedy implements Serializable {

    @SerializedName("codBarraEan")
    @Expose
    private String codBarraEan;
    @SerializedName("principioAtivo")
    @Expose
    private String principioAtivo;
    @SerializedName("cnpj")
    @Expose
    private String cnpj;
    @SerializedName("laboratorio")
    @Expose
    private String laboratorio;
    @SerializedName("codGgrem")
    @Expose
    private String codGgrem;
    @SerializedName("registro")
    @Expose
    private String registro;
    @SerializedName("produto")
    @Expose
    private String produto;
    @SerializedName("apresentacao")
    @Expose
    private String apresentacao;
    @SerializedName("classeTerapeutica")
    @Expose
    private String classeTerapeutica;
    @SerializedName("precoLiberado")
    @Expose
    private String precoLiberado;
    @SerializedName("pf0")
    @Expose
    private Double pf0;
    @SerializedName("pf12")
    @Expose
    private Double pf12;
    @SerializedName("pf17")
    @Expose
    private Double pf17;
    @SerializedName("pf17Alc")
    @Expose
    private Double pf17Alc;
    @SerializedName("pf175")
    @Expose
    private Double pf175;
    @SerializedName("pf175Alc")
    @Expose
    private Double pf175Alc;
    @SerializedName("pf18")
    @Expose
    private Double pf18;
    @SerializedName("pf18Alc")
    @Expose
    private Double pf18Alc;
    @SerializedName("pf20")
    @Expose
    private Double pf20;
    @SerializedName("pmc0")
    @Expose
    private Double pmc0;
    @SerializedName("pmc12")
    @Expose
    private Double pmc12;
    @SerializedName("pmc17")
    @Expose
    private Double pmc17;
    @SerializedName("pmc17Alc")
    @Expose
    private Double pmc17Alc;
    @SerializedName("pmc175")
    @Expose
    private Double pmc175;
    @SerializedName("pmc175Alc")
    @Expose
    private Double pmc175Alc;
    @SerializedName("pmc18")
    @Expose
    private Double pmc18;
    @SerializedName("pmc18Alc")
    @Expose
    private Double pmc18Alc;
    @SerializedName("pmc20")
    @Expose
    private Double pmc20;
    @SerializedName("restricao")
    @Expose
    private String restricao;
    @SerializedName("cap")
    @Expose
    private String cap;
    @SerializedName("confaz87")
    @Expose
    private String confaz87;
    @SerializedName("ultimaAlteracao")
    @Expose
    private String ultimaAlteracao;
    @SerializedName("cod")
    @Expose
    private Integer cod;

    /**
     *
     * @return
     * The codBarraEan
     */
    public String getCodBarraEan() {
        return codBarraEan;
    }

    /**
     *
     * @param codBarraEan
     * The codBarraEan
     */
    public void setCodBarraEan(String codBarraEan) {
        this.codBarraEan = codBarraEan;
    }

    /**
     *
     * @return
     * The principioAtivo
     */
    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    /**
     *
     * @param principioAtivo
     * The principioAtivo
     */
    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

    /**
     *
     * @return
     * The cnpj
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     *
     * @param cnpj
     * The cnpj
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     *
     * @return
     * The laboratorio
     */
    public String getLaboratorio() {
        return laboratorio;
    }

    /**
     *
     * @param laboratorio
     * The laboratorio
     */
    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    /**
     *
     * @return
     * The codGgrem
     */
    public String getCodGgrem() {
        return codGgrem;
    }

    /**
     *
     * @param codGgrem
     * The codGgrem
     */
    public void setCodGgrem(String codGgrem) {
        this.codGgrem = codGgrem;
    }

    /**
     *
     * @return
     * The registro
     */
    public String getRegistro() {
        return registro;
    }

    /**
     *
     * @param registro
     * The registro
     */
    public void setRegistro(String registro) {
        this.registro = registro;
    }

    /**
     *
     * @return
     * The produto
     */
    public String getProduto() {
        return produto;
    }

    /**
     *
     * @param produto
     * The produto
     */
    public void setProduto(String produto) {
        this.produto = produto;
    }

    /**
     *
     * @return
     * The apresentacao
     */
    public String getApresentacao() {
        return apresentacao;
    }

    /**
     *
     * @param apresentacao
     * The apresentacao
     */
    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    /**
     *
     * @return
     * The classeTerapeutica
     */
    public String getClasseTerapeutica() {
        return classeTerapeutica;
    }

    /**
     *
     * @param classeTerapeutica
     * The classeTerapeutica
     */
    public void setClasseTerapeutica(String classeTerapeutica) {
        this.classeTerapeutica = classeTerapeutica;
    }

    /**
     *
     * @return
     * The precoLiberado
     */
    public String getPrecoLiberado() {
        return precoLiberado;
    }

    /**
     *
     * @param precoLiberado
     * The precoLiberado
     */
    public void setPrecoLiberado(String precoLiberado) {
        this.precoLiberado = precoLiberado;
    }

    /**
     *
     * @return
     * The pf0
     */
    public Double getPf0() {
        return pf0;
    }

    /**
     *
     * @param pf0
     * The pf0
     */
    public void setPf0(Double pf0) {
        this.pf0 = pf0;
    }

    /**
     *
     * @return
     * The pf12
     */
    public Double getPf12() {
        return pf12;
    }

    /**
     *
     * @param pf12
     * The pf12
     */
    public void setPf12(Double pf12) {
        this.pf12 = pf12;
    }

    /**
     *
     * @return
     * The pf17
     */
    public Double getPf17() {
        return pf17;
    }

    /**
     *
     * @param pf17
     * The pf17
     */
    public void setPf17(Double pf17) {
        this.pf17 = pf17;
    }

    /**
     *
     * @return
     * The pf17Alc
     */
    public Double getPf17Alc() {
        return pf17Alc;
    }

    /**
     *
     * @param pf17Alc
     * The pf17Alc
     */
    public void setPf17Alc(Double pf17Alc) {
        this.pf17Alc = pf17Alc;
    }

    /**
     *
     * @return
     * The pf175
     */
    public Double getPf175() {
        return pf175;
    }

    /**
     *
     * @param pf175
     * The pf175
     */
    public void setPf175(Double pf175) {
        this.pf175 = pf175;
    }

    /**
     *
     * @return
     * The pf175Alc
     */
    public Double getPf175Alc() {
        return pf175Alc;
    }

    /**
     *
     * @param pf175Alc
     * The pf175Alc
     */
    public void setPf175Alc(Double pf175Alc) {
        this.pf175Alc = pf175Alc;
    }

    /**
     *
     * @return
     * The pf18
     */
    public Double getPf18() {
        return pf18;
    }

    /**
     *
     * @param pf18
     * The pf18
     */
    public void setPf18(Double pf18) {
        this.pf18 = pf18;
    }

    /**
     *
     * @return
     * The pf18Alc
     */
    public Double getPf18Alc() {
        return pf18Alc;
    }

    /**
     *
     * @param pf18Alc
     * The pf18Alc
     */
    public void setPf18Alc(Double pf18Alc) {
        this.pf18Alc = pf18Alc;
    }

    /**
     *
     * @return
     * The pf20
     */
    public Double getPf20() {
        return pf20;
    }

    /**
     *
     * @param pf20
     * The pf20
     */
    public void setPf20(Double pf20) {
        this.pf20 = pf20;
    }

    /**
     *
     * @return
     * The pmc0
     */
    public Double getPmc0() {
        return pmc0;
    }

    /**
     *
     * @param pmc0
     * The pmc0
     */
    public void setPmc0(Double pmc0) {
        this.pmc0 = pmc0;
    }

    /**
     *
     * @return
     * The pmc12
     */
    public Double getPmc12() {
        return pmc12;
    }

    /**
     *
     * @param pmc12
     * The pmc12
     */
    public void setPmc12(Double pmc12) {
        this.pmc12 = pmc12;
    }

    /**
     *
     * @return
     * The pmc17
     */
    public Double getPmc17() {
        return pmc17;
    }

    /**
     *
     * @param pmc17
     * The pmc17
     */
    public void setPmc17(Double pmc17) {
        this.pmc17 = pmc17;
    }

    /**
     *
     * @return
     * The pmc17Alc
     */
    public Double getPmc17Alc() {
        return pmc17Alc;
    }

    /**
     *
     * @param pmc17Alc
     * The pmc17Alc
     */
    public void setPmc17Alc(Double pmc17Alc) {
        this.pmc17Alc = pmc17Alc;
    }

    /**
     *
     * @return
     * The pmc175
     */
    public Double getPmc175() {
        return pmc175;
    }

    /**
     *
     * @param pmc175
     * The pmc175
     */
    public void setPmc175(Double pmc175) {
        this.pmc175 = pmc175;
    }

    /**
     *
     * @return
     * The pmc175Alc
     */
    public Double getPmc175Alc() {
        return pmc175Alc;
    }

    /**
     *
     * @param pmc175Alc
     * The pmc175Alc
     */
    public void setPmc175Alc(Double pmc175Alc) {
        this.pmc175Alc = pmc175Alc;
    }

    /**
     *
     * @return
     * The pmc18
     */
    public Double getPmc18() {
        return pmc18;
    }

    /**
     *
     * @param pmc18
     * The pmc18
     */
    public void setPmc18(Double pmc18) {
        this.pmc18 = pmc18;
    }

    /**
     *
     * @return
     * The pmc18Alc
     */
    public Double getPmc18Alc() {
        return pmc18Alc;
    }

    /**
     *
     * @param pmc18Alc
     * The pmc18Alc
     */
    public void setPmc18Alc(Double pmc18Alc) {
        this.pmc18Alc = pmc18Alc;
    }

    /**
     *
     * @return
     * The pmc20
     */
    public Double getPmc20() {
        return pmc20;
    }

    /**
     *
     * @param pmc20
     * The pmc20
     */
    public void setPmc20(Double pmc20) {
        this.pmc20 = pmc20;
    }

    /**
     *
     * @return
     * The restricao
     */
    public String getRestricao() {
        return restricao;
    }

    /**
     *
     * @param restricao
     * The restricao
     */
    public void setRestricao(String restricao) {
        this.restricao = restricao;
    }

    /**
     *
     * @return
     * The cap
     */
    public String getCap() {
        return cap;
    }

    /**
     *
     * @param cap
     * The cap
     */
    public void setCap(String cap) {
        this.cap = cap;
    }

    /**
     *
     * @return
     * The confaz87
     */
    public String getConfaz87() {
        return confaz87;
    }

    /**
     *
     * @param confaz87
     * The confaz87
     */
    public void setConfaz87(String confaz87) {
        this.confaz87 = confaz87;
    }

    /**
     *
     * @return
     * The ultimaAlteracao
     */
    public String getUltimaAlteracao() {
        return ultimaAlteracao;
    }

    /**
     *
     * @param ultimaAlteracao
     * The ultimaAlteracao
     */
    public void setUltimaAlteracao(String ultimaAlteracao) {
        this.ultimaAlteracao = ultimaAlteracao;
    }

    /**
     *
     * @return
     * The cod
     */
    public Integer getCod() {
        return cod;
    }

    /**
     *
     * @param cod
     * The cod
     */
    public void setCod(Integer cod) {
        this.cod = cod;
    }
}
