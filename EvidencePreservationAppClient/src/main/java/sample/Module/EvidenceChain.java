package sample.Module;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by WangMingming on 2017/4/28.
 */
public class EvidenceChain implements Serializable{
    private static final long serialVersionUID = 19231L;
    private ArrayList<String> chainOfDigest = new ArrayList<>();


    public ArrayList<String> getChainOfDigest() {
        return chainOfDigest;
    }

    public void setChainOfDigest(ArrayList<String> chainOfDigest) {
        this.chainOfDigest = chainOfDigest;
    }

    public void chainAdd(Evidence evidence){
        String evidenceDigest = evidence.getFinalDigest();
        if(this.chainOfDigest.contains(evidenceDigest)){
            return;
        }
        chainOfDigest.add(evidenceDigest);
    }

    public void chainRemove(Evidence evidence){
        String evidenceDigest = evidence.getFinalDigest();
        if(this.chainOfDigest.contains(evidenceDigest)){
            chainOfDigest.remove(evidenceDigest);
        }
    }

    public boolean searchChainOfDigest(String digest){
        return chainOfDigest.contains(digest);
    }
}
