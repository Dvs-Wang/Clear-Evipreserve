package sample.Module.Share.Massage;

import sample.Module.Share.Proof;

/**
 * Created by WangMingming on 2017/3/28.
 */
public class UploadSuccessMsg extends Message {
    private Proof proof;
    private boolean isLightService = false;

    public UploadSuccessMsg(){
        this.setType(MsgType.UPLOADSUC);
    }

    public Proof getProof() {
        return proof;
    }

    public void setProof(Proof proof) {
        this.proof = proof;
    }

    public boolean isLightService() {
        return isLightService;
    }

    public void setLightService(boolean lightService) {
        isLightService = lightService;
    }
}
