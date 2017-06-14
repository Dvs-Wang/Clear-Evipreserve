package sample.Module.Share.Massage;

import java.util.Date;

/**
 * Created by WangMingming on 2017/4/1.
 */
public class ForensicsAns extends Message {
    private boolean isExisted;
    private String txHash;
    private Date timestamp;
    private boolean isConfirmed;
    private boolean isComplete = true;
    private String seedDigest;
    private String eckeyBounded;
    public ForensicsAns(){
        this.setType(MsgType.FORANS);
    }

    public boolean isExisted() {
        return isExisted;
    }

    public void setExisted(boolean existed) {
        isExisted = existed;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setSeedDigest(String seedDigest) {
        this.seedDigest = seedDigest;
    }

    public String getSeedDigest() {
        return seedDigest;
    }

    public String getEckeyBounded() {
        return eckeyBounded;
    }

    public void setEckeyBounded(String eckeyBounded) {
        this.eckeyBounded = eckeyBounded;
    }
}
