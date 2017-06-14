package sample.Module.Share;

import javafx.beans.property.*;
import org.bitcoinj.core.Sha256Hash;
import Method.*;

import java.io.*;
import java.time.LocalDate;
import java.util.Random;

/**
 * Created by WangMingming on 2017/3/5.
 */
public class Evidence {
    private final StringProperty name;
    private final ObjectProperty<Sha256Hash> digest;
    private final StringProperty type;
    private final ObjectProperty<LocalDate> produceDate;
    private final StringProperty usage;
    private final BooleanProperty isUpload;
    private final BooleanProperty isConfirmed;
    /*
    *following properties nullable in the class
    * */
    private final ObjectProperty<Person> relatedPerson1;
    private final ObjectProperty<Person> relatedPerson2;
    private final StringProperty extraExplaination;
    private final ObjectProperty<Person> submitter;

    //following property generated later by the evidence complete
    private final StringProperty finalDigest;


    public Evidence(){
        this.name = new SimpleStringProperty();
        this.digest = new SimpleObjectProperty<Sha256Hash>();
        this.produceDate = new SimpleObjectProperty<LocalDate>();
        this.extraExplaination = new SimpleStringProperty("");
        this.relatedPerson1 = new SimpleObjectProperty<Person>();
        this.relatedPerson2 = new SimpleObjectProperty<Person>();
        this.submitter = new SimpleObjectProperty<Person>();
        this.type = new SimpleStringProperty("");
        this.usage = new SimpleStringProperty("");
        this.isUpload = new SimpleBooleanProperty(false);
        this.isConfirmed = new SimpleBooleanProperty(false);
        this.finalDigest = new SimpleStringProperty("");
    }

    public StringProperty nameProperty(){
        return name;
    }

    public BooleanProperty isUploadProperty(){
        return isUpload;
    }

    public BooleanProperty isConfirmedPorperty(){
        return isConfirmed;
    }

    public StringProperty finalDigestProperty(){
        return finalDigest;
    }

    public void setIsUpload(boolean isUpload){ this.isUpload.set(isUpload);}

    public void setIsConfirmed(boolean isConfirmed){this.isConfirmed.set(isConfirmed);}

    public void setName(String nameS){
        this.name.set(nameS);
    }

    public void setDigest(Sha256Hash digestS){
        this.digest.set(digestS);
    }

    public void setType(String typeS){
        this.type.set(typeS);
    }

    public void setProduceDate(LocalDate dateS){
        this.produceDate.set(dateS);
    }

    public void setRelatedPerson1(Person relatedPersonS){
        this.relatedPerson1.set(relatedPersonS);
    }

    public void setRelatedPerson2(Person relatedPersonS){
        this.relatedPerson2.set(relatedPersonS);
    }

    public void setSubmitter(Person submitterS){
        this.submitter.set(submitterS);
    }

    public void setUsage(String usageS){
        this.usage.set(usageS);
    }

    public void setExtraExplaination(String extraExplainationS){
        this.extraExplaination.set(extraExplainationS);
    }

    public void setFinalDigest(String finalDigest){
        this.finalDigest.set(finalDigest);
    }

    public String getName(){
        return name.get();
    }

    public Sha256Hash getDigest(){
        return digest.get();
    }

    public String getProduceDate(){
        return UtilsUsable.format(produceDate.get());
    }

    public String getType(){
        return type.get();
    }

    public Boolean getIsConfirmed(){
        return this.isConfirmed.getValue();
    }

    public Boolean getIsUpload(){
        return this.isUpload.getValue();
    }

    public String getFinalDigest(){
        return this.finalDigest.getValue();
    }

    public String getRelatedPerson1(){
        return relatedPerson1.get().toString();
    }

    public String getRelatedPerson2(){
        return relatedPerson2.get().toString();
    }

    public String getSubmitter(){
        return submitter.get().toString();
    }

    public String getExtraEplaination(){
        return extraExplaination.get();
    }

    public String getUsage(){return usage.get();}


    /**
     * serialize the evidence to a unencrypted file
     * @return
     */
    public int toFile(){
        Random r = new Random();
        int random = r.nextInt();
        File file = new File("src/main/java/sample/Data/" + Integer.toString(random) + ".evidence");
        while (file.exists()){
            random = r.nextInt();
            file = new File("src/main/java/sample/Data/" + Integer.toString(random) + ".evidence");
        }
        try{
            file.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(name.get());
            oos.writeObject(digest.get());
            oos.writeObject(type.get());
            oos.writeObject(produceDate.get());
            oos.writeObject(usage.get());
            oos.writeObject(relatedPerson1.get());
            oos.writeObject(relatedPerson2.get());
            oos.writeObject(submitter.get());
            oos.writeObject(extraExplaination.get());
            oos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return random;
    }

    /**
     * encrypt the evidence to a temp file
     * @param password
     * @return
     */
    public File toEcryptedFile(String password){
        try{
            File enfile = File.createTempFile(this.finalDigest.get(),"evidence");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(enfile));
            oos.writeObject(name.get());
            oos.writeObject(digest.get());
            oos.writeObject(type.get());
            oos.writeObject(produceDate.get());
            oos.writeObject(usage.get());
            oos.writeBoolean(isConfirmed.get());
            oos.writeBoolean(isUpload.get());
            oos.writeObject(relatedPerson1.get());
            oos.writeObject(relatedPerson2.get());
            oos.writeObject(submitter.get());
            oos.writeObject(extraExplaination.get());
            oos.close();
            return UtilsUsable.encryptFile(enfile,"evidence",password);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * deserialize the evidence from a unencrypted file
     * @param file
     * @return
     */
    public static Evidence fromFile(File file){
        Evidence evi = new Evidence();
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            evi.name.set((String)ois.readObject());
            evi.digest.set((Sha256Hash)ois.readObject());
            evi.type.set((String)ois.readObject());
            evi.produceDate.set((LocalDate)ois.readObject());
            evi.usage.set((String)ois.readObject());
            evi.relatedPerson1.set((Person)ois.readObject());
            evi.relatedPerson2.set((Person)ois.readObject());
            evi.submitter.set((Person)ois.readObject());
            evi.extraExplaination.set((String)ois.readObject());
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return evi;
    }

    public static Evidence fromEncryptedFile(File file,String password){
        File temp = UtilsUsable.decryptFile(file,"evidence",password);
        Evidence evi = new Evidence();
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp));
            evi.name.set((String)ois.readObject());
            evi.digest.set((Sha256Hash)ois.readObject());
            evi.type.set((String)ois.readObject());
            evi.produceDate.set((LocalDate)ois.readObject());
            evi.usage.set((String)ois.readObject());
            evi.isConfirmed.set(ois.readBoolean());
            evi.isUpload.set(ois.readBoolean());
            evi.relatedPerson1.set((Person)ois.readObject());
            evi.relatedPerson2.set((Person)ois.readObject());
            evi.submitter.set((Person)ois.readObject());
            evi.extraExplaination.set((String)ois.readObject());
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return evi;
    }
}



