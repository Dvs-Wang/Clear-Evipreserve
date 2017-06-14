import com.subgraph.orchid.encoders.Hex;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.lang.reflect.Parameter;

/**
 * Created by WangMingming on 2017/4/2.
 */
public class BitcoinTxTest {
    public static void main(String args[]){
        String s = "304402201df9e034de4a05f3b6a38703b7a02464eefe01c8eac27f7d53d060779ae06631022022c85b57cf6e485bf46a8f5f5b10a13e26660a4694e603d00d849b5866506584";
        ECKey.ECDSASignature ecKey = TransactionSignature.decodeFromDER(org.spongycastle.util.encoders.Hex.decode(s));
        System.out.println(ecKey);




    }
}
