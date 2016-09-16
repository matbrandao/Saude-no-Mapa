package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Establishment implements Serializable {

    @SerializedName("codCnes")
    @Expose
    private Integer codCnes;
    @SerializedName("codUnidade")
    @Expose
    private String codUnidade;
    @SerializedName("cnpj")
    @Expose
    private String cnpj;
    @SerializedName("codIbge")
    @Expose
    private Integer codIbge;
    @SerializedName("nomeFantasia")
    @Expose
    private String nomeFantasia;
    @SerializedName("natureza")
    @Expose
    private String natureza;
    @SerializedName("tipoUnidade")
    @Expose
    private String tipoUnidade;
    @SerializedName("esferaAdministrativa")
    @Expose
    private String esferaAdministrativa;
    @SerializedName("vinculoSus")
    @Expose
    private String vinculoSus;
    @SerializedName("retencao")
    @Expose
    private String retencao;
    @SerializedName("fluxoClientela")
    @Expose
    private String fluxoClientela;
    @SerializedName("origemGeografica")
    @Expose
    private String origemGeografica;
    @SerializedName("temAtendimentoUrgencia")
    @Expose
    private String temAtendimentoUrgencia;
    @SerializedName("temAtendimentoAmbulatorial")
    @Expose
    private String temAtendimentoAmbulatorial;
    @SerializedName("temCentroCirurgico")
    @Expose
    private String temCentroCirurgico;
    @SerializedName("temObstetra")
    @Expose
    private String temObstetra;
    @SerializedName("temNeoNatal")
    @Expose
    private String temNeoNatal;
    @SerializedName("temDialise")
    @Expose
    private String temDialise;
    @SerializedName("descricaoCompleta")
    @Expose
    private String descricaoCompleta;
    @SerializedName("tipoUnidadeCnes")
    @Expose
    private String tipoUnidadeCnes;
    @SerializedName("categoriaUnidade")
    @Expose
    private String categoriaUnidade;
    @SerializedName("logradouro")
    @Expose
    private String logradouro;
    @SerializedName("numero")
    @Expose
    private String numero;
    @SerializedName("bairro")
    @Expose
    private String bairro;
    @SerializedName("cidade")
    @Expose
    private String cidade;
    @SerializedName("uf")
    @Expose
    private String uf;
    @SerializedName("cep")
    @Expose
    private String cep;
    @SerializedName("telefone")
    @Expose
    private String telefone;
    @SerializedName("turnoAtendimento")
    @Expose
    private String turnoAtendimento;
    @SerializedName("lat")
    @Expose
    private Double latitude;
    @SerializedName("long")
    @Expose
    private Double longitude;

    /**
     * 
     * @return
     *     The codCnes
     */
    public Integer getCodCnes() {
        return codCnes;
    }

    /**
     * 
     * @param codCnes
     *     The codCnes
     */
    public void setCodCnes(Integer codCnes) {
        this.codCnes = codCnes;
    }

    /**
     * 
     * @return
     *     The codUnidade
     */
    public String getCodUnidade() {
        return codUnidade;
    }

    /**
     * 
     * @param codUnidade
     *     The codUnidade
     */
    public void setCodUnidade(String codUnidade) {
        this.codUnidade = codUnidade;
    }

    /**
     * 
     * @return
     *     The codIbge
     */
    public Integer getCodIbge() {
        return codIbge;
    }

    /**
     * 
     * @param codIbge
     *     The codIbge
     */
    public void setCodIbge(Integer codIbge) {
        this.codIbge = codIbge;
    }

    /**
     * 
     * @return
     *     The nomeFantasia
     */
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    /**
     * 
     * @param nomeFantasia
     *     The nomeFantasia
     */
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    /**
     * 
     * @return
     *     The natureza
     */
    public String getNatureza() {
        return natureza;
    }

    /**
     * 
     * @param natureza
     *     The natureza
     */
    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    /**
     * 
     * @return
     *     The tipoUnidade
     */
    public String getTipoUnidade() {
        return tipoUnidade;
    }

    /**
     * 
     * @param tipoUnidade
     *     The tipoUnidade
     */
    public void setTipoUnidade(String tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    /**
     * 
     * @return
     *     The esferaAdministrativa
     */
    public String getEsferaAdministrativa() {
        return esferaAdministrativa;
    }

    /**
     * 
     * @param esferaAdministrativa
     *     The esferaAdministrativa
     */
    public void setEsferaAdministrativa(String esferaAdministrativa) {
        this.esferaAdministrativa = esferaAdministrativa;
    }

    /**
     * 
     * @return
     *     The vinculoSus
     */
    public String getVinculoSus() {
        return vinculoSus;
    }

    /**
     * 
     * @param vinculoSus
     *     The vinculoSus
     */
    public void setVinculoSus(String vinculoSus) {
        this.vinculoSus = vinculoSus;
    }

    /**
     * 
     * @return
     *     The retencao
     */
    public String getRetencao() {
        return retencao;
    }

    /**
     * 
     * @param retencao
     *     The retencao
     */
    public void setRetencao(String retencao) {
        this.retencao = retencao;
    }

    /**
     * 
     * @return
     *     The fluxoClientela
     */
    public String getFluxoClientela() {
        return fluxoClientela;
    }

    /**
     * 
     * @param fluxoClientela
     *     The fluxoClientela
     */
    public void setFluxoClientela(String fluxoClientela) {
        this.fluxoClientela = fluxoClientela;
    }

    /**
     * 
     * @return
     *     The origemGeografica
     */
    public String getOrigemGeografica() {
        return origemGeografica;
    }

    /**
     * 
     * @param origemGeografica
     *     The origemGeografica
     */
    public void setOrigemGeografica(String origemGeografica) {
        this.origemGeografica = origemGeografica;
    }

    /**
     * 
     * @return
     *     The temAtendimentoUrgencia
     */
    public String getTemAtendimentoUrgencia() {
        return temAtendimentoUrgencia;
    }

    /**
     * 
     * @param temAtendimentoUrgencia
     *     The temAtendimentoUrgencia
     */
    public void setTemAtendimentoUrgencia(String temAtendimentoUrgencia) {
        this.temAtendimentoUrgencia = temAtendimentoUrgencia;
    }

    /**
     * 
     * @return
     *     The temAtendimentoAmbulatorial
     */
    public String getTemAtendimentoAmbulatorial() {
        return temAtendimentoAmbulatorial;
    }

    /**
     * 
     * @param temAtendimentoAmbulatorial
     *     The temAtendimentoAmbulatorial
     */
    public void setTemAtendimentoAmbulatorial(String temAtendimentoAmbulatorial) {
        this.temAtendimentoAmbulatorial = temAtendimentoAmbulatorial;
    }

    /**
     * 
     * @return
     *     The temCentroCirurgico
     */
    public String getTemCentroCirurgico() {
        return temCentroCirurgico;
    }

    /**
     * 
     * @param temCentroCirurgico
     *     The temCentroCirurgico
     */
    public void setTemCentroCirurgico(String temCentroCirurgico) {
        this.temCentroCirurgico = temCentroCirurgico;
    }

    /**
     * 
     * @return
     *     The temObstetra
     */
    public String getTemObstetra() {
        return temObstetra;
    }

    /**
     * 
     * @param temObstetra
     *     The temObstetra
     */
    public void setTemObstetra(String temObstetra) {
        this.temObstetra = temObstetra;
    }

    /**
     * 
     * @return
     *     The temNeoNatal
     */
    public String getTemNeoNatal() {
        return temNeoNatal;
    }

    /**
     * 
     * @param temNeoNatal
     *     The temNeoNatal
     */
    public void setTemNeoNatal(String temNeoNatal) {
        this.temNeoNatal = temNeoNatal;
    }

    /**
     * 
     * @return
     *     The temDialise
     */
    public String getTemDialise() {
        return temDialise;
    }

    /**
     * 
     * @param temDialise
     *     The temDialise
     */
    public void setTemDialise(String temDialise) {
        this.temDialise = temDialise;
    }

    /**
     * 
     * @return
     *     The descricaoCompleta
     */
    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    /**
     * 
     * @param descricaoCompleta
     *     The descricaoCompleta
     */
    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    /**
     * 
     * @return
     *     The tipoUnidadeCnes
     */
    public String getTipoUnidadeCnes() {
        return tipoUnidadeCnes;
    }

    /**
     * 
     * @param tipoUnidadeCnes
     *     The tipoUnidadeCnes
     */
    public void setTipoUnidadeCnes(String tipoUnidadeCnes) {
        this.tipoUnidadeCnes = tipoUnidadeCnes;
    }

    /**
     * 
     * @return
     *     The categoriaUnidade
     */
    public String getCategoriaUnidade() {
        return categoriaUnidade;
    }

    /**
     * 
     * @param categoriaUnidade
     *     The categoriaUnidade
     */
    public void setCategoriaUnidade(String categoriaUnidade) {
        this.categoriaUnidade = categoriaUnidade;
    }

    /**
     * 
     * @return
     *     The logradouro
     */
    public String getLogradouro() {
        return logradouro;
    }

    /**
     * 
     * @param logradouro
     *     The logradouro
     */
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    /**
     * 
     * @return
     *     The numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * 
     * @param numero
     *     The numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * 
     * @return
     *     The bairro
     */
    public String getBairro() {
        return bairro;
    }

    /**
     * 
     * @param bairro
     *     The bairro
     */
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    /**
     * 
     * @return
     *     The cidade
     */
    public String getCidade() {
        return cidade;
    }

    /**
     * 
     * @param cidade
     *     The cidade
     */
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    /**
     * 
     * @return
     *     The uf
     */
    public String getUf() {
        return uf;
    }

    /**
     * 
     * @param uf
     *     The uf
     */
    public void setUf(String uf) {
        this.uf = uf;
    }

    /**
     * 
     * @return
     *     The cep
     */
    public String getCep() {
        return cep;
    }

    /**
     * 
     * @param cep
     *     The cep
     */
    public void setCep(String cep) {
        this.cep = cep;
    }

    /**
     * 
     * @return
     *     The telefone
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * 
     * @param telefone
     *     The telefone
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * 
     * @return
     *     The turnoAtendimento
     */
    public String getTurnoAtendimento() {
        return turnoAtendimento;
    }

    /**
     * 
     * @param turnoAtendimento
     *     The turnoAtendimento
     */
    public void setTurnoAtendimento(String turnoAtendimento) {
        this.turnoAtendimento = turnoAtendimento;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(Double lat) {
        this.latitude = lat;
    }

    /**
     * 
     * @return
     *     The _long
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param _long
     *     The long
     */
    public void setLongitude(Double _long) {
        this.longitude = _long;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
