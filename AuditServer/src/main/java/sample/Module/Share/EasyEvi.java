package sample.Module.Share;

import java.io.Serializable;

/**
 * Created by WangMingming on 2017/5/12.
 */
public class EasyEvi implements Serializable{
    private static final long serialVersionUID = 19329L;
    private String name;
    private String finalDigest;
    private String addressBounded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFinalDigest() {
        return finalDigest;
    }

    public void setFinalDigest(String finalDigest) {
        this.finalDigest = finalDigest;
    }

    public String getAddressBounded() {
        return addressBounded;
    }

    public void setAddressBounded(String addressBounded) {
        this.addressBounded = addressBounded;
    }

    @Override
    public boolean equals(Object obj) {
        return ((EasyEvi)obj).getFinalDigest().equals(this.finalDigest);
    }

    @Override
    public String toString() {
        return name + "\t" + finalDigest + "\t" + addressBounded;
    }
}
