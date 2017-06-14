package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/5/7.
 */
public class Authlog extends Message {
    private String password;
    public Authlog(){
        this.setType(MsgType.AUTHLOG);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
