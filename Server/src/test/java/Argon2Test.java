import com.subgraph.orchid.encoders.Hex;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.BasicKeyChain;

/**
 * Created by WangMingming on 2017/3/16.
 */
public class Argon2Test {
    public static void main(String args[]){
        String password = "nihaochindsfasda123";
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d,32,32);
        String hash = argon2.hash(2,65536,10,password);
        System.out.print(argon2.verify(hash,password));
        System.out.print(hash.length());
    }
}
