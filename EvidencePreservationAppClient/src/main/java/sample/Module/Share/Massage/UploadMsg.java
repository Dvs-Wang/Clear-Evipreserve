package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/3/28.
 */
public class UploadMsg extends Message {
    private String ECkeyPbKey;
    private String evidence;
    private String finalDiggest;
    private String password;
    private String parentDiggest;
    private boolean isLightWeightService = false;

    public String getFinalDiggest() {
        return finalDiggest;
    }

    public void setFinalDiggest(String finalDiggest) {
        this.finalDiggest = finalDiggest;
    }

    public UploadMsg(){
        this.setType(MsgType.UPLOAD);
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getECkeyPbKey() {
        return ECkeyPbKey;
    }

    public void setECkeyPbKey(String ECkeyPbKey) {
        this.ECkeyPbKey = ECkeyPbKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLightWeightService() {
        return isLightWeightService;
    }

    public void setLightWeightService(boolean lightWeightService) {
        isLightWeightService = lightWeightService;
    }

    public String getParentDiggest() {
        return parentDiggest;
    }

    public void setParentDiggest(String parentDiggest) {
        this.parentDiggest = parentDiggest;
    }
}
