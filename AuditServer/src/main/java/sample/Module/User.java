package sample.Module;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import sample.Module.Evidence;

import java.util.HashMap;

/**
 * Created by WangMingming on 2017/3/9.
 */
public class User {
    private String email;
    private String saltAddedPassword;
    private HashMap<Sha256Hash, Evidence> handledEvidences;
    private ECKey encryptedEckey;

    public User(String email){
        this.email = email;
    }

    public User(String email,String saltAddedPassword,ECKey encryptedEckey){
        this.email = email;
        this.saltAddedPassword = saltAddedPassword;
        this.encryptedEckey = encryptedEckey;
    }

    public String getEmail(){
        return this.email;
    }

    public String getSaltAddedPassword(){
        return this.saltAddedPassword;
    }

    public ECKey getEncryptedEckey(){
        return this.encryptedEckey;
    }

    public void addEvidence(Evidence e){
        Sha256Hash digest = e.getDigest();
        handledEvidences.put(digest,e);
    }

    public Evidence getEvidence(Sha256Hash digest){
        return handledEvidences.get(digest);
    }


}
