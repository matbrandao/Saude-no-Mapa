package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mateus Brandão on 26/09/2016.
 */
public class Post {
    @Expose
    private Autor autor;
    @Expose
    private long codObjetoDestino;
    @Expose
    private PostType tipo;

    public Post(Autor autor, long codObjetoDestino, PostType tipo) {
        this.autor = autor;
        this.codObjetoDestino = codObjetoDestino;
        this.tipo = tipo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public long getCodObjetoDestino() {
        return codObjetoDestino;
    }

    public void setCodObjetoDestino(long codObjetoDestino) {
        this.codObjetoDestino = codObjetoDestino;
    }

    public PostType getTipo() {
        return tipo;
    }

    public void setTipo(PostType tipo) {
        this.tipo = tipo;
    }
}
