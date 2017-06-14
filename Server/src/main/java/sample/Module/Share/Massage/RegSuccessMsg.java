package sample.Module.Share.Massage;

import org.bitcoinj.core.ECKey;

/**
 * Created by WangMingming on 2017/3/12.
 */
public class RegSuccessMsg extends Message{
    private String ecKey;
    public RegSuccessMsg(){
        this.setType(MsgType.REGSUCCESS);
    }

    public void setEcKey(String ecKey){
        this.ecKey = ecKey;
    }

    public String getEcKey(){
        return ecKey;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" +ecKey;
    }
}

