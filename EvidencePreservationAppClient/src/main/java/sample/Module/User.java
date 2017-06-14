package sample.Module;

import org.bitcoinj.core.Sha256Hash;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by WangMingming on 2017/3/9.
 */
public class User {
    private String email;
    private String password;
    public HashMap<Sha256Hash,Evidence> handledEvidences = new HashMap<>();
    private String encryptedEckey;
    public HashSet<EvidenceChain> evidenceChains = new HashSet<>();
    public User(){}
    public User(String email){
        this.email = email;
    }

    public User(String email,String password,String encryptedEckey){
        this.email = email;
        this.password = password;
        this.encryptedEckey = encryptedEckey;
    }

    public String getEmail(){
        return this.email;
    }

    public String getSaltAddedPassword(){
        return this.password;
    }

    public String getEncryptedEckey(){
        return this.encryptedEckey;
    }

    public void setEncryptedEckey(String eckey){this.encryptedEckey = eckey;}
    public void setEmail(String email){this.email = email;}
    public void setPassword(String password){this.password = password;}

    public void addEvidence(Evidence e){
        Sha256Hash digest = e.getDigest();
        handledEvidences.put(digest,e);
    }

    public Evidence getEvidence(Sha256Hash digest){
        return handledEvidences.get(digest);
    }

}
