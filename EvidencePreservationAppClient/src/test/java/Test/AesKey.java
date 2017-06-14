package Test;

import org.bitcoinj.crypto.KeyCrypterScrypt;

/**
 * Created by WangMingming on 2017/3/22.
 */
public class AesKey {
    public static void main(String args[]){
        String s = "godddd";
        KeyCrypterScrypt kcs = new KeyCrypterScrypt();
        byte[] aes = kcs.deriveKey(s).getKey();
        System.out.print(aes.length);
    }

}
