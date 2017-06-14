package sample.Module.Share.Massage;
import sample.Module.User;

/**
 * Created by WangMingming on 2017/3/9.
 */
public class RegisterMsg extends Message {
    private String password;
    public RegisterMsg(){
        this.setType(MsgType.REGISTER);
    }

    public RegisterMsg(User user){
        super(user);
        this.setType(MsgType.REGISTER);
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public String toString() {
        return super.toString() + "password:" + password.toString();
    }
}
