package com.wang.serverDb;
/**
 * Created by WangMingming on 2017/3/15.
 */

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class ServerDao {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("JavaHelps");
    /**
     *create a user by its register information
     * @param eckey
     * @param password
     * @param email
     */
    public void createCU(String email, String password, String eckey) {
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            ClientUser clu = new ClientUser();
            clu.setEmail(email);
            clu.setSaltAddedPassword(password);
            clu.setEckeyEncrypted(eckey);
            manager.persist(clu);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }
    /**
     *delete the evidenceChain, only for debug use!
     * @param id
     * @forTest
     */
    public void deleteEC(int id){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            EvidenceChain du = entityManager.find(EvidenceChain.class,id);
            entityManager.remove(du);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    /**
     *delete the user by its email
     * @param email
     * @forTest
     */
    private void deleteCU(String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            ClientUser du = entityManager.find(ClientUser.class,email);
            entityManager.remove(du);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }
    /**
     *check whether the email had been registered
     * @param email
     * @return
     */
    public boolean searchCU(String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from ClientUser s where s.email =:dodod",ClientUser.class);
            query.setParameter("dodod",email);
            List du = query.getResultList();
            transaction.commit();
            //ClientUser du = entityManager.find(ClientUser.class,email);
            if (du.isEmpty()){
                return false;
            }else {
                return true;
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }finally {
            entityManager.close();
        }
    }

    /**
     * create evidence chain directly
     * @param evidenceChain
     */
    public void createEC(EvidenceChain evidenceChain){
        createPojo(evidenceChain,2);
    }

    public void createARP(AuthReply authReply){
        createPojo(authReply,4);
    }

    /**
     * pojo creator
     * @param object
     * @param type
     */
    private void createPojo(Object object,int type){
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = manager.getTransaction();
            transaction.begin();
            switch (type){
                case 0:
                    manager.persist((Evidence)object);
                    break;
                case 1:
                    manager.persist((CombinedEvidence)object);
                    break;
                case 2:
                    manager.persist((EvidenceChain)object);
                    break;
                case 3:
                    manager.persist((AuthInstitution)object);
                    break;
                case 4:
                    manager.persist((AuthReply)object);
                    break;
                default:break;
            }
            transaction.commit();
        } catch (Exception etd) {
            if (transaction != null) {
                transaction.rollback();
            }
            etd.printStackTrace();
        } finally {
            manager.close();
        }
    }

    /**
     * create authInstitution Directly
     * @param authInstitution
     */
    public void createAuth(AuthInstitution authInstitution){
        createPojo(authInstitution,3);
    }

    /**
     * log the combined evidence to the database
     * @param digest
     * @param seedDigest
     * @param digestNum
     */
    public void createCED(String digest,String seedDigest,int digestNum){
        byte isConfirmed = 0;
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try{
            transaction = manager.getTransaction();
            transaction.begin();
            CombinedEvidence ced = new CombinedEvidence();
            ced.setIsConfirmed(isConfirmed);
            ced.setDigest(digest);
            ced.setDigestNum(digestNum);
            ced.setSeedDigest(seedDigest);
            manager.persist(ced);
            transaction.commit();
        }catch (Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }finally {
            manager.close();
        }
    }
    /**
     *log the evidence to the database
     * @param digest
     * @param email
     * @param message
     */
    public void createEd(String digest,String email,String message){
        byte isConfirmed = 0;
        byte isPayed = 0;
        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();
            Evidence ed = new Evidence();
            ed.setDigest(digest);
            ed.setEmail(email);
            ed.setIsConfirmed(isConfirmed);
            ed.setMessage(message);
            ed.setIsPayed(isPayed);
            manager.persist(ed);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            manager.close();
        }
    }

    /**
     * create evidence simply by the modified evidence,
     * only can be called by the method modifyEvidence()
     * @param ed
     */
    private void createEd(Evidence ed){
        createPojo(ed,0);
    }

    /**
     * delete the evidence by the giving digest and email,
     * if more than one evidences meet the requirement, delete them all
     * @param digest
     * @param email
     */
    public void deleteEd(String digest,String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from Evidence s where s.email =:dodod and s.digest =:dg",Evidence.class);
            query.setParameter("dg",digest);
            query.setParameter("dodod",email);
            List du = query.getResultList();
            for (Object i:du){
                entityManager.remove((Evidence)i);
            }
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    /**
     * debug only, delete CombinedEvidence by searching its digest
     * @param digest
     */
    public void deleteCED(String digest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from CombinedEvidence s where s.digest =:dg",CombinedEvidence.class);
            query.setParameter("dg",digest);
            List du = query.getResultList();
            for (Object i:du){
                entityManager.remove((CombinedEvidence)i);
            }
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    public void deleteARP(String auditDigest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from AuthReply s where s.auditDigest =:dg",AuthReply.class);
            query.setParameter("dg",auditDigest);
            List du = query.getResultList();
            for (Object i:du){
                entityManager.remove((AuthReply)i);
            }
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    /**delete authInstitution by its id
     * @param Id
     */
    public void deleteAuth(String Id){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from AuthInstitution s where s.id =:dg",AuthInstitution.class);
            query.setParameter("dg",Id);
            List du = query.getResultList();
            for (Object i:du){
                entityManager.remove((AuthInstitution)i);
            }
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }
    /**
     *get the clientuser info only according to the email
     * @param email
     * @return
     */
    public ClientUser getClientUserOnly(String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try{
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery("FROM ClientUser s where s.email =:em",ClientUser.class);
            query.setParameter("em",email);
            List du  = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (ClientUser) du.get(0);
            }

        }catch (Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    /**
     * get evidence only by its digest
     * @param digest
     * @return
     */
    public Evidence getEvidence(String digest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from Evidence s where s.digest =:dg",Evidence.class);
            query.setParameter("dg",digest);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (Evidence)du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    /**
     * get authInstitution by its Id
     * @param Id
     * @return
     */
    public AuthInstitution getAuth(String Id){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from AuthInstitution s where s.id =:dg",AuthInstitution.class);
            query.setParameter("dg",Id);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (AuthInstitution) du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    public AuthReply getARP(String finalDigest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from AuthReply s where s.evidenceDigest =:dg",AuthReply.class);
            query.setParameter("dg",finalDigest);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (AuthReply) du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }

    }
    public AuthReply getARPByKey(String auditDigest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from AuthReply s where s.auditDigest =:dg",AuthReply.class);
            query.setParameter("dg",auditDigest);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (AuthReply) du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }

    }

    /**
     * get the evidence information by its digest and email which connected to the user,
     * most usable when change the writable state of the evidence
     * @param digest
     * @param email
     * @return
     */
    public Evidence getEvidenceInfoOnly(String digest,String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from Evidence s where s.email =:dodod and s.digest =:dg",Evidence.class);
            query.setParameter("dg",digest);
            query.setParameter("dodod",email);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (Evidence)du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    /**
     * modify the evidence,
     * importantly: digest, email and message can't be modified!
     * @param evi
     * @return
     */
    public boolean modifyEvidence(Evidence evi){
        String digest = evi.getDigest();
        String email = evi.getEmail();
        Evidence mid = getEvidenceInfoOnly(digest,email);
        if(mid == null){
            System.out.println("No such evidence!");
            return false;
        }else {
            if(mid.getMessage().equals(evi.getMessage())){
                deleteEd(digest,email);
                createEd(evi);
                return true;
            }else {
                System.out.println("Invalid modification!");
                return false;
            }
        }
    }

    /**
     * get all the evidences from the given user
     * @param email
     * @return
     */
    public ArrayList<Evidence> getEvidenceFromTheUser(String email){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try{
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery("FROM Evidence s where s.email =:em",Evidence.class);
            query.setParameter("em",email);
            List du  = query.getResultList();
            if (du.isEmpty()){
                transaction.commit();
                return null;
            }else {
                ArrayList<Evidence> ret = new ArrayList<>();
                for(Object i:du){
                    ret.add((Evidence)i);
                }
                transaction.commit();
                return ret;
            }
        }catch (Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    /**
     * show the whole data base,
     * debug only!
     */
    public void showDatabase(){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query quser = entityManager.createQuery("SELECT s FROM ClientUser s",ClientUser.class);
            List listuser = quser.getResultList();
            if (!listuser.isEmpty()){
                for (Object i:listuser){
                    System.out.println(i);
                    System.out.println("---------------evidence from the user-------------------------");
                    String email = ((ClientUser)i).getEmail();
                    Query qevi = entityManager.createQuery("SELECT s FROM Evidence s WHERE s.email =:em",Evidence.class);
                    qevi.setParameter("em",email);
                    List listevi = qevi.getResultList();
                    if(!listevi.isEmpty()){
                        for (Object j:listevi){
                            System.out.println(((Evidence)j));
                        }
                    }
                    System.out.println("---------------------------------------------------------------");
                }
            }
            System.out.println("-----------------------------combinedEvidence-------------------------------");
            Query qcomEvi = entityManager.createQuery("SELECT s FROM CombinedEvidence s",CombinedEvidence.class);
            List listqComEvi = qcomEvi.getResultList();
            if(!listqComEvi.isEmpty()){
                for(Object z:listqComEvi){
                    System.out.println((CombinedEvidence)z);
                    System.out.println("-------------------------------------------------------------------------------");
                }
            }
            System.out.println("---------------------------combinedEvidence--end---------------------------------");

            System.out.println("-----------------------------evidenceChain-------------------------------");
            Query qchain = entityManager.createQuery("SELECT s FROM EvidenceChain s",EvidenceChain.class);
            List listChain = qchain.getResultList();
            if(!listChain.isEmpty()){
                for(Object z:listChain){
                    System.out.println((EvidenceChain)z);
                    System.out.println("-------------------------------------------------------------------------------");
                }
            }
            System.out.println("---------------------------evidenceChain--end---------------------------------");
            System.out.println("-----------------------------authInstitution-------------------------------");
            Query qauth = entityManager.createQuery("SELECT s FROM AuthInstitution s",AuthInstitution.class);
            List listAuth = qauth.getResultList();
            if(!listAuth.isEmpty()){
                for(Object z:listAuth){
                    System.out.println((AuthInstitution)z);
                    System.out.println("---------------authReply from the institution-------------------------");
                    String name = ((AuthInstitution)z).getId();
                    Query qevi = entityManager.createQuery("SELECT s FROM AuthReply s WHERE s.institutionBody =:em",AuthReply.class);
                    qevi.setParameter("em",name);
                    List listevi = qevi.getResultList();
                    if(!listevi.isEmpty()){
                        for (Object j:listevi){
                            System.out.println(((AuthReply)j));
                        }
                    }
                    System.out.println("---------------------------------------------------------------");
                }
            }
            System.out.println("---------------------------authInstitution--end---------------------------------");
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    /**
     * clear the whole database,
     * @test use only
     */
    public void cleanDatabase(){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query quser = entityManager.createQuery("SELECT s FROM ClientUser s",ClientUser.class);
            List listuser = quser.getResultList();
            if (!listuser.isEmpty()){
                for (Object i:listuser){
                    String email = ((ClientUser)i).getEmail();
                    entityManager.remove(i);
                    Query qevi = entityManager.createQuery("SELECT s FROM Evidence s WHERE s.email =:em",Evidence.class);
                    qevi.setParameter("em",email);
                    List listevi = qevi.getResultList();
                    if(!listevi.isEmpty()){
                        for (Object j:listevi){
                            entityManager.remove(j);
                        }
                    }
                }
            }

            Query qcbc = entityManager.createQuery("select s from CombinedEvidence s",CombinedEvidence.class);
            List listCbc = qcbc.getResultList();
            if(!listCbc.isEmpty()){
                for(Object i:listCbc){
                    entityManager.remove(i);
                }
            }

            Query qec = entityManager.createQuery("select s from EvidenceChain s",EvidenceChain.class);
            List listEc = qec.getResultList();
            if(!listEc.isEmpty()){
                for(Object i:listEc){
                    entityManager.remove(i);
                }
            }

            Query qauth = entityManager.createQuery("select s from AuthInstitution s",AuthInstitution.class);
            List listAuth = qauth.getResultList();
            if(!listAuth.isEmpty()){
                for(Object i:listAuth){
                    entityManager.remove(i);
                }
            }

            Query qauthRep = entityManager.createQuery("select s from AuthReply s",AuthReply.class);
            List listAuthRep = qauthRep.getResultList();
            if(!listAuthRep.isEmpty()){
                for(Object i:listAuthRep){
                    entityManager.remove(i);
                }
            }
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }finally {
            entityManager.close();
        }
    }

    /**
     * get CombinedEvidence by search its root digest
     * @param digest
     * @return
     */
    public CombinedEvidence getCombinedEvidence(String digest) {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query query = entityManager.createQuery(" from CombinedEvidence s where s.digest =:dg",CombinedEvidence.class);
            query.setParameter("dg",digest);
            List du = query.getResultList();
            transaction.commit();
            if(du.isEmpty()){
                return null;
            }else {
                return (CombinedEvidence) du.get(0);
            }
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }

    /**
     * modify combined evidence
     * @param e
     * @return
     */
    public boolean modifyCombinedEvidence(CombinedEvidence e) {
        String digest = e.getDigest();
        String seedDigest = e.getSeedDigest();
        CombinedEvidence mid = getCombinedEvidence(digest);
        if(mid == null){
            System.out.println("No such combined evidence!");
            return false;
        }else {
            if(mid.getSeedDigest().equals(seedDigest)){
                deleteCED(digest);
                createCEd(e);
                return true;
            }else {
                System.out.println("Invalid modification!");
                return false;
            }
        }
    }

    public void modifyAuth(AuthInstitution authInstitution){
        deleteAuth(authInstitution.getId());
        createAuth(authInstitution);
    }

    public void modifyAuthReply(AuthReply authReply){
        deleteARP(authReply.getAuditDigest());
        createARP(authReply);
    }

    /**
     * create combined evidence simply by the pojo
     * @param e
     */
    private void createCEd(CombinedEvidence e) {
        createPojo(e,1);
    }

    public String findDigestInCombined(String oneOfTheSeedDigest){
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query qcomEvi = entityManager.createQuery("SELECT s FROM CombinedEvidence s",CombinedEvidence.class);
            List listqComEvi = qcomEvi.getResultList();
            if(!listqComEvi.isEmpty()){
                for(Object z:listqComEvi){
                    String seedDigest = ((CombinedEvidence)z).getSeedDigest();
                    if(seedDigest.contains(oneOfTheSeedDigest)){
                        transaction.commit();
                        return ((CombinedEvidence)z).getDigest();
                    }
                }
            }
            transaction.commit();
            return null;
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }

    }

    /**
     * close the serverDao
     */
    public void close(){
        ENTITY_MANAGER_FACTORY.close();
    }

    /**
     * check whether the parentEvidence already in a chain ,if so update the chain and return the end UTXO of the chain
     * if not, return null
     * @param newDigest
     * @param parentDigest
     * @param upEmail
     */
    public String checkDigestInChain(String newDigest, String parentDigest, String upEmail) {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Query qec = entityManager.createQuery("select s from EvidenceChain s where s.email =:pd",EvidenceChain.class);
            qec.setParameter("pd",upEmail);
            List listQec = qec.getResultList();
            if(!listQec.isEmpty()){
                for(Object i:listQec){
                    String digestList = ((EvidenceChain)i).getDigestList();
                    if(digestList.contains(newDigest)){
                        return "error";
                    }
                    if(digestList.contains(parentDigest)){
                        digestList = digestList + "\n" + newDigest;
                        EvidenceChain evidenceChain = new EvidenceChain();
                        evidenceChain.setEmail(upEmail);
                        evidenceChain.setDigestList(digestList);
                        deleteEC(((EvidenceChain) i).getChainId());
                        createEC(evidenceChain);
                        String[] mid = digestList.split("\n");
                        transaction.commit();
                        return mid[mid.length-2];
                    }
                }
            }

            transaction.commit();
            return null;
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }finally {
            entityManager.close();
        }
    }
}
