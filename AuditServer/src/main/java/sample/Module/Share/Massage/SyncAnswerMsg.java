package sample.Module.Share.Massage;

import sample.Module.Share.Proof;

/**
 * Created by WangMingming on 2017/3/30.
 */
public class SyncAnswerMsg extends Message {
    private Proof proof;
    private boolean isLightService = false;
    private boolean isProofComplete = false;
    public SyncAnswerMsg(){
        this.setType(MsgType.SYNCANS);
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

    public boolean isProofComplete() {
        return isProofComplete;
    }

    public void setProofComplete(boolean proofComplete) {
        isProofComplete = proofComplete;
    }
}
