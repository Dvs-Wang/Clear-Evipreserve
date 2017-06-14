package sample.Module;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.spongycastle.util.encoders.Hex;
import sample.Method.UtilsUsable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * Created by WangMingming on 2017/3/20.
 */
@XmlRootElement(name = "user")
public class UserWrapper {
    private byte[] encrptedECkey;
    private String email;
    private List<EvidenceWrapper> evidenceWrappers = new ArrayList<>();
    private HashSet<EvidenceChain> evidenceChains = new HashSet<>();

    public void UserWrapperInit(User user,String password){
        this.email = user.getEmail();
        //encrypt the Eckey by user's password.
        String eckey = user.getEncryptedEckey();
        this.encrptedECkey = UtilsUsable.encrypt(eckey,password);
        this.evidenceChains = user.evidenceChains;
        Iterator<Map.Entry<Sha256Hash, Evidence>> iter = user.handledEvidences.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = iter.next();
            Evidence val = (Evidence) (entry.getValue());
            EvidenceWrapper evidenceWrapper = new EvidenceWrapper();
            evidenceWrapper.evidenceWrapperInit(val,password);
            evidenceWrappers.add(evidenceWrapper);
        }
    }

    public User decodeFromUserWrapper(String password){
        User user = new User();
        user.setEmail(email);
        //decrypt Eckey from password
        user.evidenceChains = this.evidenceChains;
        user.setEncryptedEckey(new String(UtilsUsable.decrypt(encrptedECkey,password)));
        for(EvidenceWrapper i : evidenceWrappers){
            Evidence temp = i.decodeFromEviWrapper(password);
            user.handledEvidences.put(Sha256Hash.wrap(temp.getFinalDigest()),temp);
        }
        return user;
    }

    @XmlElement(name = "email")
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    @XmlElement(name = "encryptedECkey")
    public byte[] getEncrptedECkey(){
        return encrptedECkey;
    }
    public void setEncrptedECkey(byte[] encrptedECkey){
        this.encrptedECkey = encrptedECkey;
    }

    @XmlElement(name = "evidenceWrappers")
    public List<EvidenceWrapper> getEvidenceWrappers(){
        return evidenceWrappers;
    }
    public void setEvidenceWrappers(List<EvidenceWrapper> evidenceWrappers){
        this.evidenceWrappers = evidenceWrappers;
    }
    @XmlElement(name = "evidenceChains")
    public HashSet<EvidenceChain> getEvidenceChains() {
        return evidenceChains;
    }
    public void setEvidenceChains(HashSet<EvidenceChain> evidenceChains) {
        this.evidenceChains = evidenceChains;
    }
}
