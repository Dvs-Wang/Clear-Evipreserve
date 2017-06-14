package sample.Module.Share.Massage;


/**
 * Created by WangMingming on 2017/3/12.
 */
public class WrongPasswordMsg extends Message {
    public WrongPasswordMsg(){
        this.setType(MsgType.WRONGPASSWORD);
    }
}
