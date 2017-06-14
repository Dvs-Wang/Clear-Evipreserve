package sample.Module.Share;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangMingming on 2017/5/7.
 */
public class Institution {
    private String name;
    private String password;
    private ArrayList<String> auditPbkey = new ArrayList<>();
    private String proofHash;
    private HashMap<String,AuditAns> auditAnsHashMap = new HashMap<>();
    private ArrayList<EasyEvi>  eviMap = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getAuditPbkey() {
        return auditPbkey;
    }

    public void setAuditPbkey(ArrayList<String> auditPbkey) {
        this.auditPbkey = auditPbkey;
    }

    public String getProofHash() {
        return proofHash;
    }

    public void setProofHash(String proofHash) {
        this.proofHash = proofHash;
    }

    public HashMap<String, AuditAns> getAuditAnsHashMap() {
        return auditAnsHashMap;
    }

    public void setAuditAnsHashMap(HashMap<String, AuditAns> auditAnsHashMap) {
        this.auditAnsHashMap = auditAnsHashMap;
    }

    public void save(){
        try{
            File file = new File("src/main/java/AuditServer/Data/localFile.auth");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this.name);
            oos.writeObject(this.auditPbkey);
            oos.writeObject(this.proofHash);
            oos.writeObject(this.auditAnsHashMap);
            oos.writeObject(this.eviMap);
            oos.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Institution load(){
        try{
            Institution institution = new Institution();
            File file = new File("src/main/java/AuditServer/Data/localFile.auth");
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream(file));
            institution.setName((String)oos.readObject());
            institution.setAuditPbkey((ArrayList<String>)oos.readObject());
            institution.setProofHash((String)oos.readObject());
            institution.setAuditAnsHashMap((HashMap<String,AuditAns>)oos.readObject());
            institution.setEviMap((ArrayList<EasyEvi>)oos.readObject());
            oos.close();
            return institution;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void auditAdd(String digest,AuditAns auditAns){
        if(!auditAnsHashMap.containsKey(digest)){
            auditAnsHashMap.put(digest, auditAns);
        }
    }

    public EasyEvi getEasyEvi(String name){
        for(EasyEvi easyEvi:eviMap){
            if(easyEvi.getName().equals(name)){
                return easyEvi;
            }
        }
        return null;
    }


    public ArrayList<EasyEvi> getEviMap() {
        return eviMap;
    }

    public void setEviMap(ArrayList<EasyEvi> eviMap) {
        this.eviMap = eviMap;
    }
}
