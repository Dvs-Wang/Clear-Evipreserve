import Method.UtilsUsable;
import com.subgraph.orchid.encoders.Hex;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.TestNet3Params;
import sample.Module.Share.Massage.SyncRequestMsg;

/**
 * Created by WangMingming on 2017/3/28.
 */
public class ECPrivateKeyTest {
    public static void main(String args[]){
        ECKey ecKey = new ECKey();
        NetworkParameters networkParameters = TestNet3Params.get();
        Address address = ecKey.toAddress(networkParameters);
        System.out.println(address);

        String password = "dasdadad";
        System.out.println(ecKey);
        String PBkey = ecKey.getPublicKeyAsHex();
        System.out.println(PBkey);
        //System.out.println(ECKey.fromPrivate(Hex.decode(PRkey)).getPrivateKeyAsHex());::Hex.decode is good.

        /*byte[] en = UtilsUsable.encrypt(PRkey,password);
        byte[] de1 =  UtilsUsable.decrypt(en,password);
        String de2 = new String(de1);
        System.out.println(Utils.HEX.encode(de1));
        System.out.println(de2);*///::String is good for decode the decrypt answer

        String storeData = Utils.HEX.encode(UtilsUsable.encrypt(ecKey.getPrivateKeyAsHex(),password));
        System.out.println(ECKey.fromPrivate(Hex.decode(new String(UtilsUsable.decrypt(Utils.HEX.decode(storeData),password)))));



    }
}
