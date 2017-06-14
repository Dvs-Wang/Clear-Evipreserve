package com.wang.serverDb;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by WangMingming on 2017/3/15.
 */
@Entity
@Table(name = "evidence")
public class Evidence implements Serializable{
    private int evidenceId;
    private String digest;
    private String email;
    private String message;
    private byte isConfirmed;
    private byte isPayed;
    private Date timeStamp;
    private String txHash;

    @Id
    @Column(name = "evidenceId",unique = true)
    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    @Basic
    @Column(name = "digest",nullable = false)
    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    @Basic
    @Column(name = "email",nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "message",nullable = false)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "isConfirmed",nullable = false)
    public byte getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(byte isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Basic
    @Column(name = "isPayed",nullable = false)
    public byte getIsPayed() {
        return isPayed;
    }
    public void setIsPayed(byte isPayed) {
        this.isPayed = isPayed;
    }

    @Basic
    @Column(name = "timeStamp",nullable = true)
    public Date getTimeStamp(){
        return  timeStamp;
    }
    public void setTimeStamp(Date timeStamp){
        this.timeStamp = timeStamp;
    }

    @Basic
    @Column(name = "txHash",nullable = true)
    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    @Override
    public int hashCode() {
        int result = evidenceId;
        result = 31 * result + (digest != null ? digest.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (int) isConfirmed;
        result = 31 * result + (int) isPayed;
        return result;
    }

    @Override
    public String toString() {
        return evidenceId + "\t" + digest + "\t" + email +"\t" + message + "\t" + isConfirmed + "\t" + isPayed + "\t" + timeStamp + "\t" + txHash;
    }


}
