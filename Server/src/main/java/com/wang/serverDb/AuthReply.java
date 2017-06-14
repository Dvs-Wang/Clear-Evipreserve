package com.wang.serverDb;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by WangMingming on 2017/5/13.
 */
@Entity
@Table(name = "authreply")
public class AuthReply implements Serializable {
    private String auditDigest;
    private String evidenceDigest;
    private Date authTimestamp;
    private boolean isConfirmed = false;
    private String institutionBody;
    private String authTxHash;

    @Id
    @Column(name = "auditdigest",unique = true)
    public String getAuditDigest() {
        return auditDigest;
    }

    public void setAuditDigest(String auditDigest) {
        this.auditDigest = auditDigest;
    }
    @Basic
    @Column(name = "evidencedigest")
    public String getEvidenceDigest() {
        return evidenceDigest;
    }

    public void setEvidenceDigest(String evidenceDigest) {
        this.evidenceDigest = evidenceDigest;
    }
    @Basic
    @Column(name = "authtimestamp")
    public Date getAuthTimestamp() {
        return authTimestamp;
    }

    public void setAuthTimestamp(Date authTimestamp) {
        this.authTimestamp = authTimestamp;
    }
    @Basic
    @Column(name = "institutionbody")
    public String getInstitutionBody() {
        return institutionBody;
    }

    public void setInstitutionBody(String institutionBody) {
        this.institutionBody = institutionBody;
    }
    @Basic
    @Column(name = "authtxhash")
    public String getAuthTxHash() {
        return authTxHash;
    }

    public void setAuthTxHash(String authTxHash) {
        this.authTxHash = authTxHash;
    }
    @Basic
    @Column(name = "isconfirmed")
    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    @Override
    public String toString() {
        return auditDigest + "\t" + evidenceDigest + "\t" + authTimestamp + "\t" + institutionBody + "\t" + authTxHash + "\t" + isConfirmed;
    }
}
