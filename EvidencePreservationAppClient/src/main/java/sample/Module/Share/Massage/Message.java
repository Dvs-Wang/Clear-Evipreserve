package sample.Module.Share.Massage;



import sample.Module.User;

import java.io.Serializable;

/**
 * Created by WangMingming on 2017/3/9.
 */

public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    private String email;
    Message(){}
    Message(User user){
        super();
        this.email = user.getEmail();
    }

    public String getEmail(){
        return this.email;
    }

    public MsgType getType(){
        return this.type;
    }

    public void setType(MsgType type){
        this.type = type;
    }

    public void setEmail(User user){
        this.email = user.getEmail();
    }

    @Override
    public String toString() {
        return "type: " + type.toString() + "email: " + email.toString();
    }
}
