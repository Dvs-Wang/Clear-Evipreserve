package sample.Module.Share.Massage;

/**
 * Created by WangMingming on 2017/3/12.
 */
public class ValidateMsg extends Message {
    private String validateCode;
    private String password;

    public ValidateMsg(){
        this.setType(MsgType.VALIDATE);
    }
    public void setPassword(String password){this.password = password;}
    public String getPassword(){return password;}
    public void setValidateCode(String str){
        this.validateCode = str;
    }
    public String getValidateCode(){
        return validateCode;
    }
}
