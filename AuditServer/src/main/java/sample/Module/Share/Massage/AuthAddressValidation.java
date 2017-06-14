package sample.Module.Share.Massage;

import java.util.ArrayList;

/**
 * Created by WangMingming on 2017/5/10.
 */
public class AuthAddressValidation extends Message{
    private ArrayList<String> addressList = new ArrayList<>();
    private byte[] txProof;
    private String password;
    public AuthAddressValidation(){
        this.setType(MsgType.AUTHVALID);
    }

    public ArrayList<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(ArrayList<String> addressList) {
        this.addressList = addressList;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getTxProof() {
        return txProof;
    }

    public void setTxProof(byte[] txProof) {
        this.txProof = txProof;
    }
}
