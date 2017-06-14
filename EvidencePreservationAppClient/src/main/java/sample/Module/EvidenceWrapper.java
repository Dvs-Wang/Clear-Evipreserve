package sample.Module;

import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

/**
 * Created by WangMingming on 2017/3/23.
 */
@XmlRootElement(name = "evidence")
public class EvidenceWrapper {
    private String finalDigest;
    private byte[] encryptedEvi;

    public void evidenceWrapperInit(Evidence evidence, String password){
        this.finalDigest = evidence.getFinalDigest();
        try {
            this.encryptedEvi = FileUtils.readFileToByteArray(evidence.toEcryptedFile(password));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Evidence decodeFromEviWrapper(String password){
        Evidence evidence;
        try{
            File file = File.createTempFile("temp",".evidence");
            FileUtils.writeByteArrayToFile(file,encryptedEvi);
            evidence = Evidence.fromEncryptedFile(file,password);
            evidence.setFinalDigest(this.finalDigest);
            return evidence;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @XmlElement(name = "encryptedEvi")
    public byte[] getEncryptedEvi(){
        return encryptedEvi;
    }
    public void setEncryptedEvi(byte[] encryptedEvi){
        this.encryptedEvi = encryptedEvi;
    }
    @XmlElement(name = "finalDigest")
    public String getFinalDigest() {
        return finalDigest;
    }
    public void setFinalDigest(String finalDigest){
        this.finalDigest = finalDigest;
    }
}
