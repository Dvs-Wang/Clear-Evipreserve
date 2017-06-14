import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.signers.LocalTransactionSigner;
import org.bitcoinj.signers.TransactionSigner;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.RedeemData;

/**
 * Created by WangMingming on 2017/4/10.
 */
public class ChainTest {
    public static void main(String args[]){
        NetworkParameters params = TestNet3Params.get();
        ECKey myEcKey = new ECKey();
        Transaction tx = FakeTxBuider.createFakeTxWithoutChangeAddress(params,Coin.COIN,myEcKey.toAddress(params));
        tx.addOutput(Coin.COIN,myEcKey.toAddress(params));
        System.out.print(tx.getOutput(1));
        Transaction tx1 = new Transaction(params);
        tx1.addOutput(Coin.valueOf(70000000),new ECKey().toAddress(params));
        tx1.addSignedInput(new TransactionOutPoint(params,0,tx),tx.getOutput(0).getScriptPubKey(),myEcKey);

        System.out.println(tx1);

        try{
            tx1.getInput(0).verify(tx.getOutput(0));
            //tx1.verify();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
