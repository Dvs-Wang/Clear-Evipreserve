package com.wang.serverDb;

import com.google.common.annotations.GwtCompatible;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by WangMingming on 2017/4/26.
 */
@Entity
@Table(name = "EvidenceChain")
public class EvidenceChain implements Serializable{
    private int chainId;
    private String email;
    private String digestList;
    @Id
    @Column(name = "chainId",unique = true)
    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
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
    @Column(name = "digestList",nullable = false)
    public String getDigestList() {
        return digestList;
    }

    public void setDigestList(String digestList) {
        this.digestList = digestList;
    }

    @Override
    public String toString() {
        return chainId + "\t" + email + "    digestList:\n" + digestList;
    }
}
