package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/3/30.
 */
public class SyncRequestMsg extends Message {
    private String eviFinalDigest;
    public SyncRequestMsg(){
        this.setType(MsgType.SYNCREQUEST);
    }

    public String getEviFinalDigest() {
        return eviFinalDigest;
    }

    public void setEviFinalDigest(String eviFinalDigest) {
        this.eviFinalDigest = eviFinalDigest;
    }
}
