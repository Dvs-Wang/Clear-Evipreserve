package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/3/9.
 */
public class ReLoginMsg extends Message{
    public ReLoginMsg(){
        super();
        this.setType(MsgType.RELOGIN);
    }
}
