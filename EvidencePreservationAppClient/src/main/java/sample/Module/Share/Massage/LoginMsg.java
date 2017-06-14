package sample.Module.Share.Massage;

import sample.Module.User;

/**
 * Created by WangMingming on 2017/3/9.
 */
public class LoginMsg extends Message {
    private String password;
    public LoginMsg(){
        this.setType(MsgType.LOGIN);
    }

    public LoginMsg(User user){
        super(user);
        this.setType(MsgType.LOGIN);
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
