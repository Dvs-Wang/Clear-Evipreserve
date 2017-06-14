package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/5/13.
 */
public class AuthNoteReply extends Message{
    private String state;
    private String digest;
    public AuthNoteReply(){
        this.setType(MsgType.AUTHNOTEREP);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
