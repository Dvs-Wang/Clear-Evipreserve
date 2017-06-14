package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/5/9.
 */
public class AuthLogReply extends Message {
    private String reply;
    public AuthLogReply(){
        this.setType(MsgType.AUTHLOGREP);
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
