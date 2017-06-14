package com.wang.serverDb;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by WangMingming on 2017/5/9.
 */
@Entity
@Table(name = "authinstitution")
public class AuthInstitution implements Serializable{
    private String Id;
    private String saltAddedPassword;
    private String authPbkeyList;
    private String proofTxHash;
    private String authLog;
    private boolean isProofTxConfirmed;
    @Id
    @Column(name = "id",unique = true)
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Basic
    @Column(name = "saltaddedpassword")
    public String getSaltAddedPassword() {
        return saltAddedPassword;
    }

    public void setSaltAddedPassword(String saltAddedPassword) {
        this.saltAddedPassword = saltAddedPassword;
    }
    @Basic
    @Column(name = "authpbkeylist")
    public String getAuthPbkeyList() {
        return authPbkeyList;
    }

    public void setAuthPbkeyList(String authPbkeyList) {
        this.authPbkeyList = authPbkeyList;
    }
    @Basic
    @Column(name = "prooftxhash")
    public String getProofTxHash() {
        return proofTxHash;
    }

    public void setProofTxHash(String proofTxHash) {
        this.proofTxHash = proofTxHash;
    }
    @Basic
    @Column(name = "authlog")
    public String getAuthLog() {
        return authLog;
    }

    public void setAuthLog(String authLog) {
        this.authLog = authLog;
    }

    @Override
    public String toString() {
        return Id + "\t" + saltAddedPassword + "\nPbkeyList:\n" + authPbkeyList + "\n\tproofTxHash:" + proofTxHash + "\nauthlog:\n" + authLog + "\n" + isProofTxConfirmed;
    }
    @Basic
    @Column(name = "isprooftxconfirmed")
    public boolean isProofTxConfirmed() {
        return isProofTxConfirmed;
    }

    public void setProofTxConfirmed(boolean proofTxConfirmed) {
        isProofTxConfirmed = proofTxConfirmed;
    }
}
