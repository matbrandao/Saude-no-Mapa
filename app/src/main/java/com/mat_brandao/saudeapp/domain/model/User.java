package com.mat_brandao.saudeapp.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Mateus Brandão on 29-Aug-16.
 */

public class User extends RealmObject {
    public static final int NORMAL_LOGIN_TYPE = 0;
    public static final int FACEBOOK_LOGIN_TYPE = 1;
    public static final int GOOGLE_LOGIN_TYPE = 2;

    @SerializedName("cod")
    @Expose
    private Long id;
    @SerializedName("nomeCompleto")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("senha")
    @Expose
    private String password;
    @SerializedName("nomeUsuario")
    @Expose
    private String username;
    @SerializedName("CEP")
    @Expose
    private String cep;
    @SerializedName("sexo")
    @Expose
    private String sex;
    @SerializedName("tokenFacebook")
    @Expose
    private String facebookToken;
    @SerializedName("tokenGoogle")
    @Expose
    private String googleToken;
    @SerializedName("avatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("dataNascimento")
    @Expose
    private String birthDate;
    @SerializedName("biografia")
    @Expose
    private String bio;

    private int passwordType;
    private String appToken;
    private Long installationId;
    private Long establishmentLikePost;
    private Long remedyLikePostCode;

    public User() {
    }

    public User(String name, String email, String username, String password, String facebookToken,
                String googleToken, String cep, String sex, String avatarUrl, String birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.facebookToken = facebookToken;
        this.googleToken = googleToken;
        this.username = username;
        this.cep = cep;
        this.sex = sex;
        this.avatarUrl = avatarUrl;
        this.birthDate = birthDate;
    }

    public User(String name, String email, String username, String password, String facebookToken,
                String googleToken, String cep, String sex, String avatarUrl, String birthDate, String bio) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.facebookToken = facebookToken;
        this.googleToken = googleToken;
        this.username = username;
        this.cep = cep;
        this.sex = sex;
        this.avatarUrl = avatarUrl;
        this.birthDate = birthDate;
        this.bio = bio;
    }

    public User(String name, String email, String username, String password, String facebookToken,
                String googleToken, String cep, String sex, String birthDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.facebookToken = facebookToken;
        this.googleToken = googleToken;
        this.username = username;
        this.cep = cep;
        this.sex = sex;
        this.birthDate = birthDate;
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

    public int getPasswordType() {
        return passwordType;
    }

    public void setPasswordType(int passwordType) {
        this.passwordType = passwordType;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
    }

    public Long getEstablishmentLikePost() {
        return establishmentLikePost;
    }

    public void setEstablishmentLikePost(Long establishmentLikePost) {
        this.establishmentLikePost = establishmentLikePost;
    }

    public Long getRemedyLikePostCode() {
        return remedyLikePostCode;
    }

    public void setRemedyLikePostCode(Long remedyLikePostCode) {
        this.remedyLikePostCode = remedyLikePostCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
