package com.wang.serverDb;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by WangMingming on 2017/3/15.
 */
@Entity
@Table(name = "clientuser")
public class ClientUser implements Serializable{
    @Id
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "saltAddedPassword",nullable = false)
    private String saltAddedPassword;
    @Column(name = "eckeyEncrypted",nullable = false)
    private String eckeyEncrypted;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSaltAddedPassword() {
        return saltAddedPassword;
    }

    public void setSaltAddedPassword(String saltAddedPassword) {
        this.saltAddedPassword = saltAddedPassword;
    }

    public String getEckeyEncrypted() {
        return eckeyEncrypted;
    }

    public void setEckeyEncrypted(String eckeyEncrypted) {
        this.eckeyEncrypted = eckeyEncrypted;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (saltAddedPassword != null ? saltAddedPassword.hashCode() : 0);
        result = 31 * result + (eckeyEncrypted != null ? eckeyEncrypted.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return email + "\t" + saltAddedPassword + "\t" + eckeyEncrypted;
    }
}
