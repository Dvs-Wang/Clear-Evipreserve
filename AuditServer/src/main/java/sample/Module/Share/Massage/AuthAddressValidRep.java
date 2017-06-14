package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/5/10.
 */
public class AuthAddressValidRep extends Message {
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public  AuthAddressValidRep(){
        this.setType(MsgType.AUTHVALIDREP);
    }
}
