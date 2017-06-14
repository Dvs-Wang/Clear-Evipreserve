package sample.Module.Share;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by WangMingming on 2017/3/29.
 */
public class Proof implements Serializable{
    private static final long serialVersionUID = 19999L;
    private Date Timestamp;
    private String txHash;
    private String ECkeyBounded;
    private String seedDigest;
    private String combinedDigest;
    private String parentDigest;
    private int confirmTimes;
    transient private IntegerProperty depth;
    transient private String eviSimpleInfo;

    public Proof(){
        depth = new SimpleIntegerProperty();
    }

    public Date getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        Timestamp = timestamp;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getECkeyBounded() {
        return ECkeyBounded;
    }

    public void setECkeyBounded(String ECkeyBounded) {
        this.ECkeyBounded = ECkeyBounded;
    }

    public String getEviSimpleInfo() {
        return eviSimpleInfo;
    }

    public void setEviSimpleInfo(String eviSimpleInfo) {
        this.eviSimpleInfo = eviSimpleInfo;
    }

    public int getDepth() {
        return depth.get();
    }

    public IntegerProperty depthProperty() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth.set(depth);
    }

    public int getConfirmTimes() {
        return confirmTimes;
    }

    public void setConfirmTimes(int confirmTimes) {
        this.confirmTimes = confirmTimes;
    }

    public String getSeedDigest() {
        return seedDigest;
    }

    public void setSeedDigest(String seedDigest) {
        this.seedDigest = seedDigest;
    }

    public String getCombinedDigest() {
        return combinedDigest;
    }

    public void setCombinedDigest(String combinedDigest) {
        this.combinedDigest = combinedDigest;
    }

    public String getParentDigest() {
        return parentDigest;
    }

    public void setParentDigest(String parentDigest) {
        this.parentDigest = parentDigest;
    }
}
