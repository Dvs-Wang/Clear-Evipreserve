package sample.Controller;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.Sha256Hash;
import sample.Main;
import sample.Method.UtilsUsable;
import sample.Module.EvidenceChain;
import sample.Module.Grape;
import sample.Module.Evidence;
import sample.Module.Share.AuditAnswer;
import sample.Module.Share.Massage.SyncRequestMsg;
import sample.Module.Share.Massage.UploadMsg;
import sample.Module.Share.Proof;
import sample.Module.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by WangMingming on 2017/3/20.
 */
public class ShowEvidenceDetailController {
    public Label iDiggestLb;
    public Label fDiggestLb;
    public Label nameLb;
    public Label produceDateLb;
    public Label submitterLb;
    public Label typeLb;
    public Label confirmationTimesLb;
    public Label txIndexLb;
    public Label eckeyBoundedLb;
    public Label timestampLb;
    public Label proofIndexLb;
    public Label proofDb;
    public Label bodyLb;
    public Label proofTimestampLb;
    public Circle eviLocal;
    public ImageView bitcoinNetwork;
    public Label relatedPersonLb;
    public VBox moduleVbox;
    public Button recoverBtn;
    public Button syncBtn;
    public MenuButton uploadBtn;
    public VBox operationVbox;
    public TextArea usageTextArea;
    public TextArea extraExArea;
    public Label googleIcon;
    public Label copyIcon;
    public Label transformIcon;
    public Pane eviChain;
    public CustomMenuItem distinctItem;
    public CustomMenuItem lightWeightItem;
    public Label lwGoogleIcon;
    public Label lwCopyIcon1;
    public Label lwConfirmationTimesLb;
    public Label lwTxIndexLb;
    public Label lwCombinedDigest;
    public Label lwTimestampLb;
    public TextArea lwSeedDigestTextArea;
    public Menu add2ChainMenu;
    public CustomMenuItem chainOnMenuItem;
    public Label googleIcon2;


    private boolean transformState = false;
    private boolean REGTEST_MODE = false;

    private Main main;
    private Stage stage;
    private Evidence evidence;
    private User priUser;
    private Proof proof;
    final public String safeLink = "https://live.blockcypher.com/btc-testnet/tx/";
    private double CENTRE_X = 79.0;
    private double CENTRE_Y = 63.0;
    private double RADIUS = 15.0;

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
        //expand the evidence details and fill in the table
        nameLb.setText(evidence.getName());
        fDiggestLb.setText(evidence.getFinalDigest());
        iDiggestLb.setText(evidence.getDigest().toString());
        produceDateLb.setText(evidence.getProduceDate());
        typeLb.setText(evidence.getType());
        usageTextArea.setText(evidence.getUsage());
        relatedPersonLb.setText(evidence.getRelatedPerson1() + "\n" + evidence.getRelatedPerson2());
        submitterLb.setText(evidence.getSubmitter());
        extraExArea.setText(evidence.getExtraEplaination());
        if(evidence.getIsUpload()){
            if(evidence.getLightProof()!=null){
                Proof proof = evidence.getLightProof();
                lwTxIndexLb.setText(proof.getTxHash());
                lwCombinedDigest.setText(proof.getCombinedDigest());
                if(proof.getConfirmTimes() >= 3){
                    lwConfirmationTimesLb.setText(" 3 OR 3+ (CONFIRMED)");
                }else {
                    lwConfirmationTimesLb.setText(String.valueOf(proof.getConfirmTimes()));
                }
                if(proof.getTimestamp()!=null){
                    lwTimestampLb.setText(proof.getTimestamp().toString());
                }
                if(proof.getConfirmTimes() == 0){
                    lwTimestampLb.setText("Not Confirmed This Time.");
                }
                lwSeedDigestTextArea.setText(proof.getSeedDigest());

            }
            if(!(evidence.getTxHash().length()==0||evidence.getTxHash()==null)){
                if(evidence.getConfirmTimes() >= 3){
                    confirmationTimesLb.setText(" 3 OR 3+ (CONFIRMED)");
                }else {
                    confirmationTimesLb.setText(String.valueOf(evidence.getConfirmTimes()));
                }
                txIndexLb.setText(evidence.getTxHash());
                eckeyBoundedLb.setText(evidence.getECkeyBounded());
                if(evidence.getTimeStamp()!= null){
                    timestampLb.setText(evidence.getTimeStamp().toString());
                }
                if(evidence.getConfirmTimes() == 0){
                    timestampLb.setText("Not Confirmed This Time.");
                }
                if(evidence.getAuditAns()!=null){
                    AuditAnswer auditAnswer = evidence.getAuditAns();
                    proofIndexLb.setText(auditAnswer.getAuditTxHash());
                    proofTimestampLb.setText(auditAnswer.getAuthTimestamp()==null?"Not Confirmed":auditAnswer.getAuthTimestamp().toString());
                    bodyLb.setText(auditAnswer.getInstitutionName());
                    proofDb.setText(auditAnswer.getAuthDigest());
                }
            }
        }
        setChainStructureShape();
        setAdd2ChainMenu();
    }

    private void setAdd2ChainMenu() {
        add2ChainMenu.getItems().add(new SeparatorMenuItem());
        if((!evidence.getIsUpload())|| (evidence.getIsUpload() && evidence.getTxHash().length()==0)){
            HashMap<String,String> eviParentMap = main.r.catchChainOnList();
            Iterator<Map.Entry<String,String>> iterator = eviParentMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                CustomMenuItem menuItem = new CustomMenuItem(new Label(entry.getValue()));
                Tooltip.install(menuItem.getContent(),new Tooltip("digest: " + entry.getKey()));
                menuItem.setDisable(false);
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        handleChainOnUpdate(entry.getKey());
                    }
                });
                add2ChainMenu.getItems().add(menuItem);
            }
            if(add2ChainMenu.getItems().size()==2){
                add2ChainMenu.getItems().add(new MenuItem("No evidence which meet conditions!"));
                //add2ChainMenu.getItems().get(2).setDisable(true);
            }
        }else {
            add2ChainMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    RootInterfaceController.alertForSth("Current evidence has received distinct service and can't be moved to a evidence chain!");
                }
            });
        }

    }

    private void handleChainOnUpdate(String finalParentDigest) {
        UploadMsg uploadMsg = new UploadMsg();
        try{
            byte[] mid = FileUtils.readFileToByteArray(evidence.toEcryptedFile(priUser.getSaltAddedPassword()));
            uploadMsg.setEvidence(org.bitcoinj.core.Utils.HEX.encode(mid));
            uploadMsg.setEmail(priUser);
            uploadMsg.setPassword(priUser.getSaltAddedPassword());
            uploadMsg.setFinalDiggest(evidence.getFinalDigest());
            uploadMsg.setECkeyPbKey(priUser.getEncryptedEckey());
            uploadMsg.setParentDiggest(finalParentDigest);
        }catch (IOException e){
            e.printStackTrace();
        }
        main.r.ch.writeAndFlush(uploadMsg);

    }


    @FXML
    private void initialize(){
        //init the elements of the stage
        uploadBtn.setPopupSide(Side.RIGHT);
        AwesomeDude.setIcon(googleIcon, AwesomeIcon.GOOGLE_PLUS);
        AwesomeDude.setIcon(googleIcon2, AwesomeIcon.GOOGLE_PLUS);
        AwesomeDude.setIcon(lwGoogleIcon, AwesomeIcon.GOOGLE_PLUS);
        AwesomeDude.setIcon(copyIcon,AwesomeIcon.COPY);
        AwesomeDude.setIcon(lwCopyIcon1,AwesomeIcon.COPY);
        AwesomeDude.setIcon(transformIcon,AwesomeIcon.MAIL_FORWARD);
        Tooltip.install(copyIcon,new Tooltip("Click to copy the tx index!"));
        Tooltip.install(lwCopyIcon1,new Tooltip("Click to copy the tx index!"));
        Tooltip.install(transformIcon,new Tooltip("Click to transform eckey to address!"));
        Tooltip.install(googleIcon,new Tooltip("Click to switch to the safe link to search your upload message!"));
        Tooltip.install(googleIcon2,new Tooltip("Click to switch to the safe link to search your audit message!"));
        Tooltip.install(lwGoogleIcon,new Tooltip("Click to switch to the safe link to search your upload message!"));

        Tooltip.install(chainOnMenuItem.getContent(),new Tooltip("Click to build your evidence chain after the appointed evidence!"));
        Tooltip.install(recoverBtn,new Tooltip("Recover your evidence assistant file if needed"));
        Tooltip.install(syncBtn,new Tooltip("Synchronize the info with the server"));
        Tooltip.install(distinctItem.getContent(),new Tooltip("Transparent and efficient upload service with continuation support"));
        Tooltip.install(lightWeightItem.getContent(),new Tooltip("Low cost POE upload service without continuation support"));
        usageTextArea.setPrefColumnCount(16);
        extraExArea.setPrefColumnCount(16);
        //css to amend next few days
        String image = getClass().getClassLoader().getResource("View/Resources/back.jpg").toExternalForm();
        ImageView imageView = new ImageView(image);
        operationVbox.setStyle("-fx-background-image: url('" + image + "');" + "-fx-background-size: 150 600; ");
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void getController(){}

    public void handleUpload(ActionEvent actionEvent) {
        UploadMsg uploadMsg = new UploadMsg();
        try{
            byte[] mid = FileUtils.readFileToByteArray(evidence.toEcryptedFile(priUser.getSaltAddedPassword()));
            uploadMsg.setEvidence(org.bitcoinj.core.Utils.HEX.encode(mid));
            uploadMsg.setECkeyPbKey(priUser.getEncryptedEckey());
            uploadMsg.setFinalDiggest(evidence.getFinalDigest());
            uploadMsg.setPassword(priUser.getSaltAddedPassword());
            main.r.ch.writeAndFlush(uploadMsg);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void handleSync(ActionEvent actionEvent) {
        if(evidence.getIsUpload()){
            SyncRequestMsg syncRequestMsg = new SyncRequestMsg();
            syncRequestMsg.setEviFinalDigest(evidence.getFinalDigest());
            main.r.ch.writeAndFlush(syncRequestMsg);
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Warning:");
            alert.setContentText("The evidence hasn't been uploaded!"+"\n   Please update first!");
        }

    }

    public void handleRecover(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the folder to save your recovery file:");
        File directory = directoryChooser.showDialog(main.getPrimaryStage());
        if(directory == null){
            return;
        }else {
            int nonceNum = evidence.toFile();
            File readFrom = new File("src/main/java/sample/Data/" + nonceNum + ".evidence");
            File writeTo = new File(directory.getAbsolutePath() + "\\" + nonceNum + ".evidence");
            try {
                if(!writeTo.exists()){
                    writeTo.createNewFile();
                }
                UtilsUsable.customBufferBufferedStreamCopy(readFrom,writeTo);
            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }

    public void setPriUser(User priUser) {
        this.priUser = priUser;
    }

    public Proof getProof() {
        return proof;
    }

    public void getSafeLink(MouseEvent mouseEvent) {
        getSafeLinkLb(txIndexLb);
    }

    public void onCopyTx(MouseEvent mouseEvent) {
        onCopyTxLb(txIndexLb);
    }

    public void onTransform(MouseEvent mouseEvent) {
        if(evidence.getIsUpload()){
            if(transformState){
                eckeyBoundedLb.setText(evidence.getECkeyBounded());
                Tooltip.install(transformIcon,new Tooltip("Click to transform eckey to address!"));
                transformState = false;
            }else {
                eckeyBoundedLb.setText(priUser.getEncryptedEckey());
                Tooltip.install(transformIcon,new Tooltip("Click to transform address to eckey!"));
                transformState = true;
            }
        }

    }

    private void setChainStructureShape(){
        Tooltip.install(eviLocal,new Tooltip("Local Evidence" +  "\nIs Updated: " + evidence.getIsUpload()));
        bitcoinNetwork.setImage(new Image(getClass().getClassLoader().getResourceAsStream("View/Resources/bitcoinNetwork.jpg")));
        Tooltip.install(bitcoinNetwork,new Tooltip("The bitcoin network" ));
        if(evidence.getIsUpload()){
            if(evidence.getTxHash().length()!=0 && evidence.getTxHash() != null){
                eviChain.getChildren().add(Grape.ConnectedLine(91,65,144,65));
                EvidenceChain evidenceChain = main.r.searchEvidenceChainExisted(evidence.getFinalDigest());
                if(evidenceChain!=null){
                    //the evidence contains in a chain
                    int size = evidenceChain.getChainOfDigest().size();
                    double llength = 376/size-20;
                    for(int i = 0;i < size;i++){
                        double dx = 250+20*i+llength*i;
                        Evidence evidence = main.r.getUserOnline().handledEvidences.get(Sha256Hash.wrap(evidenceChain.getChainOfDigest().get(i)));
                        eviChain.getChildren().add(Grape.ConnectedLine(dx,40,dx+llength,40));
                        eviChain.getChildren().add(Grape.EvidenceCircle(dx+llength+10,40,10,evidence));
                        if(evidence.getAuditAns()!=null){
                            AuditAnswer auditAnswer = evidence.getAuditAns();
                            Circle circle = new Circle(dx+10+llength,90,10,auditAnswer.isConfirmed()?Color.GREEN : Color.RED);
                            eviChain.getChildren().add(Grape.ConnectedLine(dx+llength+10,50,dx+llength+10,90));
                            Tooltip.install(circle,new Tooltip("Audit Reply: " + auditAnswer.getAuthDigest() +  "\nFrom Institution:  "+ auditAnswer.getInstitutionName() + "\nIn tx: "+ auditAnswer.getAuditTxHash() + "\nIs Confirmed: " + auditAnswer.isConfirmed()));
                            eviChain.getChildren().add(circle);
                        }
                    }
                }else {
                    eviChain.getChildren().add(Grape.ConnectedLine(250,40,303,40));
                    eviChain.getChildren().add(Grape.EvidenceCircle(313,40,10,evidence));
                    if(evidence.getAuditAns()!=null){
                        AuditAnswer auditAnswer = evidence.getAuditAns();
                        Circle circle = new Circle(313,90,10,auditAnswer.isConfirmed()?Color.GREEN : Color.RED);
                        eviChain.getChildren().add(Grape.ConnectedLine(313,50,313,90));
                        Tooltip.install(circle,new Tooltip("Audit Reply: " + auditAnswer.getAuthDigest() +  "\nFrom Institution:  "+ auditAnswer.getInstitutionName() + "\nIn tx: "+ auditAnswer.getAuditTxHash() + "\nIs Confirmed: " + auditAnswer.isConfirmed()));
                        eviChain.getChildren().add(circle);
                    }
                }
            }else if(evidence.getLightProof()!=null && evidence.getLightProof().getTxHash().length()!=0 && evidence.getLightProof().getTxHash()!=null) {
                eviChain.getChildren().add(Grape.ConnectedLine(91,65,144,65));
                eviChain.getChildren().add(Grape.ConnectedLine(250,65,303,65));
                eviChain.getChildren().add(Grape.EvidenceCircle(313,65,10,evidence));

            }
        }
    }

    

    public void handleLightUpload(ActionEvent actionEvent) {
        UploadMsg uploadMsg = new UploadMsg();
        uploadMsg.setLightWeightService(true);
        uploadMsg.setFinalDiggest(evidence.getFinalDigest());
        uploadMsg.setPassword(priUser.getSaltAddedPassword());
        main.r.ch.writeAndFlush(uploadMsg);
    }

    public void getLwSafeLink(MouseEvent mouseEvent) {
        getSafeLinkLb(lwTxIndexLb);
    }

    private void getSafeLinkLb(Label label){
        if(label.getText().length()==0||label.getText()==null){
            return;
        }
        String safeLinkForTx = safeLink + label.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(safeLinkForTx);
        clipboard.setContent(content);
        UtilsUsable.click2go(safeLinkForTx);
    }

    public void onLwCopyTx(MouseEvent mouseEvent) {
        onCopyTxLb(lwTxIndexLb);
    }

    private void onCopyTxLb(Label label){
        if(label.getText().length()==0||label.getText()==null){
            return;
        }
        String txIndex = label.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txIndex);
        clipboard.setContent(content);

    }

    public void getAuditSafeLink(MouseEvent mouseEvent) {
        getSafeLinkLb(proofIndexLb);
    }
}
