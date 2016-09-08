package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Mateus Brandão on 29-Aug-16.
 */

public class User extends RealmObject {
    @SerializedName("nomeCompleto")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("senha")
    private String password;
    @SerializedName("nomeUsuario")
    private String username;
    @SerializedName("CEP")
    private String cep;
    @SerializedName("sexo")
    private String sex;

    public User() {
    }

    public User(String name, String email, String username, String password, String cep, String sex) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.username = username;
        this.cep = cep;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
