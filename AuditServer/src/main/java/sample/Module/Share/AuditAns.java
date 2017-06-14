package sample.Module.Share;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by WangMingming on 2017/5/7.
 */
public class AuditAns implements Serializable{
    private static final long serialVersionUID = 6007L;
    private String auditDigest;
    private String finalDigest;
    private String auditAddress;
    private String txHash;
    private Date timestamp;
    private boolean isConfirmed = false;
    private boolean isWitnessed = false;

    public String getAuditDigest() {
        return auditDigest;
    }

    public void setAuditDigest(String auditDigest) {
        this.auditDigest = auditDigest;
    }

    public String getFinalDigest() {
        return finalDigest;
    }

    public void setFinalDigest(String finalDigest) {
        this.finalDigest = finalDigest;
    }

    public String getAuditAddress() {
        return auditAddress;
    }

    public void setAuditAddress(String auditAddress) {
        this.auditAddress = auditAddress;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isWitnessed() {
        return isWitnessed;
    }

    public void setWitnessed(boolean witnessed) {
        isWitnessed = witnessed;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return auditAddress + "\t" + finalDigest + "\t" + auditAddress + txHash + "\t" +timestamp + "\t" + isConfirmed + "\t" + isWitnessed;
    }
}
