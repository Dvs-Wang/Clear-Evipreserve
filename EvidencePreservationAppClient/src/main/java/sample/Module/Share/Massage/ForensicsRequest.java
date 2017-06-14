package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/4/1.
 */
public class ForensicsRequest extends Message {
    private String eviHash;
    private boolean isLightService;

    public ForensicsRequest(){
        this.setType(MsgType.FORREQ);
    }

    public String getEviHash() {
        return eviHash;
    }

    public void setEviHash(String eviHash) {
        this.eviHash = eviHash;
    }

    public boolean isLightService() {
        return isLightService;
    }

    public void setLightService(boolean lightService) {
        isLightService = lightService;
    }
}
