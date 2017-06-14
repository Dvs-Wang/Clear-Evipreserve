package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/5/13.
 */
public class AuthNote extends Message{
    private byte[] authTx;
    private String authDigest;
    private String ecPbkey;
    private String finalDigest;
    public AuthNote(){
        this.setType(MsgType.AUTHNOTE);
    }

    public byte[] getAuthTx() {
        return authTx;
    }

    public void setAuthTx(byte[] authTx) {
        this.authTx = authTx;
    }

    public String getAuthDigest() {
        return authDigest;
    }

    public void setAuthDigest(String authDigest) {
        this.authDigest = authDigest;
    }

    public String getEcPbkey() {
        return ecPbkey;
    }

    public void setEcPbkey(String ecPbkey) {
        this.ecPbkey = ecPbkey;
    }

    public String getFinalDigest() {
        return finalDigest;
    }

    public void setFinalDigest(String finalDigest) {
        this.finalDigest = finalDigest;
    }
}
