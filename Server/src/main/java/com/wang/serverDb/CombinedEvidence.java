package com.wang.serverDb;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by WangMingming on 2017/4/18.
 */
@Entity
@Table(name = "combinedEvidence")
public class CombinedEvidence implements Serializable{
    private int combinedEvidenceId;
    private String digest;
    private String seedDigest;
    private Date timeStamper;
    private String txHash;
    private byte isConfirmed;
    private int digestNum;
    @Id
    @Column(name = "combinedEvidenceId",unique = true)
    public int getCombinedEvidenceId() {
        return combinedEvidenceId;
    }

    public void setCombinedEvidenceId(int combinedEvidenceId) {
        this.combinedEvidenceId = combinedEvidenceId;
    }
    @Basic
    @Column(name = "digest")
    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
    @Basic
    @Column(name = "seedDigest",nullable = false)
    public String getSeedDigest() {
        return seedDigest;
    }

    public void setSeedDigest(String seedDigest) {
        this.seedDigest = seedDigest;
    }
    @Basic
    @Column(name = "timeStamper")
    public Date getTimeStamper() {
        return timeStamper;
    }

    public void setTimeStamper(Date timeStamper) {
        this.timeStamper = timeStamper;
    }
    @Basic
    @Column(name = "txHash")
    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
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
    @Column(name = "digestNum",nullable = false)
    public int getDigestNum() {
        return digestNum;
    }

    public void setDigestNum(int digestNum) {
        this.digestNum = digestNum;
    }

    @Override
    public String toString() {
        return combinedEvidenceId + "\t" + digest + "\nseed:\n" + seedDigest + "\t" + timeStamper + "\t" + txHash + "\t" + isConfirmed + "\t" + digestNum;
    }
}
