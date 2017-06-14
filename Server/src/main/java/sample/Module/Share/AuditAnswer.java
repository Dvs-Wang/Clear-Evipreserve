package sample.Module.Share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by WangMingming on 2017/5/14.
 */
public class AuditAnswer implements Serializable{
    private static final long serialVersionUID = 6414L;
    private String auditTxHash;
    private String authDigest;
    private String institutionName;
    private Date authTimestamp;
    private String authProofHash;
    private ArrayList<String> authPbKeys = new ArrayList<>();
    private boolean isConfirmed;

    public String getAuditTxHash() {
        return auditTxHash;
    }

    public void setAuditTxHash(String auditTxHash) {
        this.auditTxHash = auditTxHash;
    }

    public String getAuthDigest() {
        return authDigest;
    }

    public void setAuthDigest(String authDigest) {
        this.authDigest = authDigest;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Date getAuthTimestamp() {
        return authTimestamp;
    }

    public void setAuthTimestamp(Date authTimestamp) {
        this.authTimestamp = authTimestamp;
    }

    public String getAuthProofHash() {
        return authProofHash;
    }

    public void setAuthProofHash(String authProofHash) {
        this.authProofHash = authProofHash;
    }

    public ArrayList<String> getAuthPbKeys() {
        return authPbKeys;
    }

    public void setAuthPbKeys(ArrayList<String> authPbKeys) {
        this.authPbKeys = authPbKeys;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
