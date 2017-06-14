/*
 * Copyright by the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package AuditServer;

import AuditServer.Method.MerkleTrees;
import AuditServer.Method.UtilsUsable;
import AuditServer.utils.ClientHandler;
import AuditServer.utils.GuiUtils;
import com.subgraph.orchid.encoders.Hex;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.utils.MonetaryFormat;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.bitcoinj.wallet.SendRequest;
import org.fxmisc.easybind.EasyBind;
import AuditServer.controls.ClickableBitcoinAddress;
import AuditServer.controls.NotificationBarPane;
import AuditServer.utils.BitcoinUIModel;
import AuditServer.utils.easing.EasingMode;
import AuditServer.utils.easing.ElasticInterpolator;
import sample.Module.Share.AuditAns;
import sample.Module.Share.EasyEvi;
import sample.Module.Evidence;
import sample.Module.Share.EvidenceOperatorCode;
import sample.Module.Share.Institution;
import sample.Module.Share.Massage.*;
import sample.Module.Share.Massage.Message;
import sample.Module.User;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static AuditServer.Main.bitcoin;
import static AuditServer.Main.instance;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
public class MainController {
    public VBox gaaVbox;
    public TextField evidenceDirectoryField;
    public Label evidenceDirectoryBtnLabel;
    public TextField evidenceIllustrateField;
    public Label evidenceIiiustrateBtnLabel;
    public TextArea resultArea;
    public TextField auditResultDirectoryField;
    public Label auditResultDirectoryBtnLabel;
    public ChoiceBox addressChoiceBox;
    public ChoiceBox evidenceChoiceBox;
    public Button testBtn;
    private Institution institution;
    public static String host = "127.0.0.1";
    final public String safeLink = "https://live.blockcypher.com/btc-testnet/tx/";
    public static int port = 7878;
    public VBox loginVbox;
    public TitledPane LI;
    public TitledPane GAA;
    public TitledPane FAA;
    public Label exLabel;
    public Group generateGroup;
    public TextField numberTextField;
    private boolean connectFlag = false;
    public HBox controlsBox;
    public Label balance;
    public Button sendMoneyOutBtn;
    public ClickableBitcoinAddress addressControl;
    public ListView<String> addressShowingListView;
    public TextField instiNameLb;
    public PasswordField passwordLb;
    private Channel ch;
    private String tempFinalDigest;
    private ObjectProperty<Message> overWriteMsg = new ReadOnlyObjectWrapper<Message>();

    private BitcoinUIModel model = new BitcoinUIModel();
    private NotificationBarPane.Item syncItem;


    // Called by FXMLLoader.
    public void initialize() {
        addressControl.setOpacity(0.0);
    }

    public void onBitcoinSetup() {
        model.setWallet(bitcoin.wallet());
        addressControl.addressProperty().bind(model.addressProperty());
        balance.textProperty().bind(EasyBind.map(model.balanceProperty(), coin -> MonetaryFormat.BTC.noCode().format(coin).toString()));
        // Don't let the user click send money when the wallet is empty.
        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));

        showBitcoinSyncMessage();
        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0) {
                readyToGoAnimation();
                if (syncItem != null) {
                    syncItem.cancel();
                    syncItem = null;
                }
            } else if (syncItem == null) {
                showBitcoinSyncMessage();
            }
        });
        //GAA.setDisable(true);
        //FAA.setDisable(true);

        testBtn.setDisable(true);
        testBtn.setVisible(false);

    }

    private void showBitcoinSyncMessage() {
        syncItem = Main.instance.notificationBar.pushItem("Synchronising with the Bitcoin network", model.syncProgressProperty());
    }

    public void sendMoneyOut(ActionEvent event) {
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
        Main.instance.overlayUI("send_money.fxml");
    }

    public void settingsClicked(ActionEvent event) {
        Main.OverlayUI<WalletSettingsController> screen = Main.instance.overlayUI("wallet_settings.fxml");
        screen.controller.initialize(null);
    }

    public void restoreFromSeedAnimation() {
        // Buttons slide out ...
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200), controlsBox);
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        // Buttons slide in and clickable address appears simultaneously.
        TranslateTransition arrive = new TranslateTransition(Duration.millis(1200), controlsBox);
        arrive.setInterpolator(new ElasticInterpolator(EasingMode.EASE_OUT, 1, 2));
        arrive.setToY(0.0);
        FadeTransition reveal = new FadeTransition(Duration.millis(1200), addressControl);
        reveal.setToValue(1.0);
        ParallelTransition group = new ParallelTransition(arrive, reveal);
        group.setDelay(NotificationBarPane.ANIM_OUT_DURATION);
        group.setCycleCount(1);
        group.play();
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }

    public void handleTest(ActionEvent actionEvent) {
        /*
        Institution institution = new Institution();
        institution.setName("某公证处");
        String s ="029eaf62c800341a63ea8c4284ba483a370b4f0569d545bd5fb641a763849f09d3\n" +
                "03bfa73d2944c094ca7167d4782797b82d10cddb1966f2113d4b50fd81479e2250\n" +
                "02820e9e2d4a9d2e163ebe9c961b1f21905549c53561e6d943bc6ef9bdb520a510\n" +
                "022053f57f9e312474673754d299454be46de794858620e596b687d05f4f0c3c14\n" +
                "03085bbfdbbc052d10efbd6a92426a744518f6b817ef96ec1fabbf334bc226ad8a\n" +
                "02fba937b4934c3c1d62bca77b3e513d375bd0fad902b8e432057152ec17502220\n" +
                "0340806a081b74dfc06b59df2db01e654287011c18d27355da160dd265c2250875\n" +
                "0387e25bf06fb9167678924ac108e1d331efcb5c3d1be77386ad91fb6e31eaed08\n" +
                "02fcc99536cf6436c162c07d4f2d9b3017c03ab4ee7b204121f0b662235a687aa2\n" +
                "02880a2ec547f444e3d50d29e8b3ef36d6c7dbd145599a2bc175d6e6a5abe945eb";
        String[] ss = s.split("\n");
        ArrayList<String> sss = new ArrayList<>();
        for(String str:ss){
            sss.add(ECKey.fromPublicOnly(org.spongycastle.util.encoders.Hex.decode(str)).toAddress(bitcoin.params()).toString());
        }
        institution.setAuditPbkey(sss);
        institution.setProofHash("718f5fef99a0e1d2a08be98529b3dbf0a60ed1bf0159629fdb007b8885146ac7");
        institution.save();*/
        institution = Institution.load();
        institution.getAuditAnsHashMap().clear();
        institution.getEviMap().clear();
        institution.save();

        //FAA.setDisable(false);

    }

    public void handleLogin(ActionEvent actionEvent) {
        if (checkLogin()) {
            if (!connectFlag) {
                LoginService loginService = new LoginService(instiNameLb.getText(), passwordLb.getText());
                loginService.setOnSucceeded(t -> ch = (Channel) t.getSource().getValue());
                loginService.start();
                loginVbox.setDisable(true);
                overWriteMsg.addListener((observable, oldValue, newValue) -> {
                    switch (overWriteMsg.get().getType()) {
                        case AUTHLOGREP:
                            String reply = ((AuthLogReply) (overWriteMsg.get())).getReply();
                            if (reply.equals("Success!")) {
                                File file = new File("src/main/java/AuditServer/Data/localFile.auth");
                                if (!file.exists()) {
                                    institution = new Institution();
                                    institution.setName(instiNameLb.getText());
                                    institution.save();
                                }
                                generateGroup.setDisable(true);
                                generateGroup.setVisible(false);
                                LI.setDisable(true);
                                GAA.setDisable(false);
                                FAA.setDisable(false);
                                institution = Institution.load();
                                institution.setPassword(passwordLb.getText());
                                ArrayList<String> addressList = institution.getAuditPbkey();
                                if (!addressList.isEmpty()) {
                                    showAuthAddress(institution);
                                    setFAA();
                                } else {
                                    generateGroup.setDisable(false);
                                    generateGroup.setVisible(true);
                                    exLabel.setText("Your institution have no auth addresses initialized!\n Please generate some to ensure the validity!");
                                }
                            } else {
                                GuiUtils.informationalAlert("warning", reply);
                                loginVbox.setDisable(false);
                            }
                            break;
                        case AUTHVALIDREP:
                            String state = ((AuthAddressValidRep) (overWriteMsg.get())).getState();
                            if (state.equals("Watched")) {
                                System.out.println(state);
                                setFAA();
                            } else if (state.equals("Confirmed")) {
                                System.out.println(state);
                            } else {
                                GuiUtils.informationalAlert("warning", "Auth Address Validation Failed!");
                            }
                            break;
                        case FORANS:
                            ForensicsAns forensicsAns = (ForensicsAns) overWriteMsg.get();
                            if (forensicsAns.isExisted()) {
                                if (!forensicsAns.isComplete()) {
                                    resultArea.setText("Light service detected on current evidence but not complete, more time to wait for further information!");
                                } else {
                                    if (forensicsAns.getTimestamp() == null) {
                                        resultArea.setText("Digest Detected on the Bitcoin Blockchain, but not been received by block yet please wait for the blockchain confirmation!(Normally less than half an hour from the evidence uploaded)");
                                    } else {
                                        resultArea.setText("Digest Detected on the Bitcoin Blockchain in tx: " + forensicsAns.getTxHash() + ".\nIntegrity and Timeliness proved before " + forensicsAns.getTimestamp() + "\n");
                                        if (forensicsAns.isConfirmed()) {
                                            String st = resultArea.getText();
                                            resultArea.setText(st + "Digest has been witnessed by more than 3+ blocks and it's seen as Non-tempered Evidence!");
                                            //load the evidence to the institution auth library
                                            File evidenceIllFilePath = new File(evidenceIllustrateField.getText());
                                            Evidence evidence = Evidence.fromFile(evidenceIllFilePath);
                                            EasyEvi easyEvi = new EasyEvi();
                                            easyEvi.setFinalDigest(tempFinalDigest);
                                            easyEvi.setName(evidence.getName());
                                            easyEvi.setAddressBounded(forensicsAns.getEckeyBounded());
                                            if (!institution.getEviMap().contains(easyEvi)) {
                                                institution.getEviMap().add(easyEvi);
                                                evidenceChoiceBox.getItems().add(evidence.getName());
                                            }
                                        } else {
                                            String st = resultArea.getText();
                                            resultArea.setText(st + "Digest has been witnessed by less than 3 blocks and need to wait for more confirmations!");
                                        }
                                        String st = resultArea.getText();
                                        resultArea.setText(st + "\nSafe link to view the eivdence on the blockchain: " + "\nhttps://live.blockcypher.com/btc-testnet/tx/" + forensicsAns.getTxHash());

                                    }
                                    if (forensicsAns.getSeedDigest() != null) {
                                        String st = resultArea.getText();
                                        resultArea.setText(st + "\nThe service type is lightweight, connected seed digest shows as follows:\n" + forensicsAns.getSeedDigest());
                                    }
                                }

                            } else {
                                resultArea.setText("No Such Digest existed on Bitcoin Blockchain!");
                            }

                            break;

                        case AUTHNOTEREP:
                            String state1 = ((AuthNoteReply)(overWriteMsg.get())).getState();
                            if (state1.equals("Watched")) {
                                System.out.println(state1);
                                AuditAns auditAns =institution.getAuditAnsHashMap().get(((AuthNoteReply)(overWriteMsg.get())).getDigest());
                                auditAns.setWitnessed(true);
                                institution.getAuditAnsHashMap().put(((AuthNoteReply)(overWriteMsg.get())).getDigest(),auditAns);
                                GuiUtils.informationalAlert("", "Authentication for Evidence " + ((AuthNoteReply)(overWriteMsg.get())).getDigest() + "\nComplete and has been witnessed by the server!");
                            } else if (state1.equals("Confirmed")) {
                                System.out.println(state1);
                            } else {
                                GuiUtils.informationalAlert("warning",((AuthNoteReply)(overWriteMsg.get())).getState());
                            }
                            break;
                        default:
                            break;
                    }
                });
                connectFlag = true;
            } else {
                Authlog authlog = new Authlog();
                authlog.setEmail(new User(instiNameLb.getText()));
                authlog.setPassword(passwordLb.getText());
                ch.writeAndFlush(authlog);
            }
        }
    }

    private boolean checkLogin() {
        if(instiNameLb.getText()==null||instiNameLb.getText().length()==0){
            GuiUtils.informationalAlert("Warning","Institution Name Cannot be empty!");
            return false;
        }else if(passwordLb.getText() == null||passwordLb.getText().length()==0){
            GuiUtils.informationalAlert("Warning","Password Cannot be empty!");
            return false;
        }else {
            return true;
        }
    }

    private void showAuthAddress(Institution institution){
        exLabel.setText("Your institution have " + institution.getAuditPbkey().size() + " auth addresses initialized!");
        ObservableList<String> addressObList = FXCollections.observableList(institution.getAuditPbkey());
        addressShowingListView.setItems(addressObList);
        HBox hBox = new HBox();
        Label label1 = new Label("Proof Tx Hash for auth address:  " + institution.getProofHash());
        Label label2 = new Label();
        AwesomeDude.setIcon(label2, AwesomeIcon.GOOGLE_PLUS);
        Tooltip.install(label2,new Tooltip("click to check the proof Tx"));
        label2.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                UtilsUsable.click2go(safeLink + institution.getProofHash());
            }
        });
        hBox.getChildren().add(label1);
        hBox.getChildren().add(label2);
        hBox.setSpacing(15);
        gaaVbox.getChildren().add(hBox);
    }

    public void generateAuditAddress(ActionEvent actionEvent) {
        int addressNum = Integer.parseInt(numberTextField.getText());
        //generate and add the keyList to the wallet
        List<ECKey> authKeyList = new ArrayList<>();
        ArrayList<String> authPbkeyList = new ArrayList<>();
        for(int i = 0;i < addressNum;i++){
            ECKey ecKey = new ECKey();
            authKeyList.add(ecKey);
            authPbkeyList.add(ecKey.getPublicKeyAsHex());
        }
        bitcoin.wallet().importKeys(authKeyList);
        //calculate the root digest of both keys and generate a null data tx to prove it
        MerkleTrees merkleTrees = new MerkleTrees(authPbkeyList);
        merkleTrees.merkle_tree();
        String root = merkleTrees.getRoot();
        Transaction txProof = new Transaction(bitcoin.params());
        txProof.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(Hex.decode(root.getBytes())));
        try{
            bitcoin.wallet().sendCoins(SendRequest.forTx(txProof));
        }catch (Exception e){
            e.printStackTrace();
        }
        AuthAddressValidation authAddressValidation = new AuthAddressValidation();
        authAddressValidation.setPassword(institution.getPassword());
        authAddressValidation.setAddressList(authPbkeyList);
        authAddressValidation.setTxProof(txProof.bitcoinSerialize());
        ch.writeAndFlush(authAddressValidation);
        institution.setAuditPbkey(authPbkeyList);
        institution.setProofHash(txProof.getHashAsString());
        institution.save();
        generateGroup.setDisable(true);
        generateGroup.setVisible(false);
        showAuthAddress(institution);
        //distribute the coins to the keys
        /*
        Transaction tx = new Transaction(bitcoin.params());
        for(int i = 0;i < addressNum;i++){
            tx.addOutput(Coin.COIN.div(20),authKeyList.get(i).toAddress(bitcoin.params()));
        }
        try{
            bitcoin.wallet().sendCoins(SendRequest.forTx(tx));
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    public void onUpForDirectory(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Upload your evidence directory:");
        File file = directoryChooser.showDialog(instance.mainWindow);
        if(file == null){
            return;
        }
        evidenceDirectoryField.setText(file.getAbsolutePath());
    }

    public void onUpForFile(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your evidence illustrate file:");
        File file = fileChooser.showOpenDialog(instance.mainWindow);
        if(file == null){
            return;
        }
        evidenceIllustrateField.setText(file.getAbsolutePath());

    }

    private void setFAA(){
        evidenceDirectoryField.setPromptText("Upload your Evidence Directory Path Here:");
        evidenceIllustrateField.setPromptText("Upload your Evidence Illustrate File Path Here:");
        AwesomeDude.setIcon(evidenceDirectoryBtnLabel, AwesomeIcon.UPLOAD);
        AwesomeDude.setIcon(evidenceIiiustrateBtnLabel, AwesomeIcon.UPLOAD);
        Tooltip.install(evidenceDirectoryBtnLabel,new Tooltip("Click to upload your evidence directory!"));
        Tooltip.install(evidenceIiiustrateBtnLabel,new Tooltip("Click to upload yout evidence illustrate file!"));
        resultArea.setPromptText("The result will be shown here!");
        resultArea.setPrefColumnCount(15);
        auditResultDirectoryField.setPromptText("Upload the Audit Result file Directory Path Here:");
        AwesomeDude.setIcon(auditResultDirectoryBtnLabel, AwesomeIcon.UPLOAD);
        Tooltip.install(auditResultDirectoryBtnLabel,new Tooltip("Click to upload the corresponding audit file directory!"));
        addressChoiceBox.setItems(FXCollections.observableList(institution.getAuditPbkey()));
        ArrayList<EasyEvi> easyEvis = institution.getEviMap();
        ArrayList<String> nameMap = new ArrayList<>();
        for(EasyEvi easyEvi:easyEvis){
            nameMap.add(easyEvi.getName());
        }
        evidenceChoiceBox.setItems(FXCollections.observableArrayList(nameMap));
        
    }

    public void forHandleUp(ActionEvent actionEvent) {
        if(evidenceDirectoryField.getText() == null || evidenceIllustrateField.getText()==null){
            GuiUtils.informationalAlert("warning","Lack evidence file." + "\nPlease complete the upload process first!");
            return;
        }
        File evidenceDirectoryPath = new File(evidenceDirectoryField.getText());
        File evidenceIllFilePath = new File(evidenceIllustrateField.getText());
        if(evidenceDirectoryPath.exists()&&evidenceIllFilePath.exists()){
            try {
                byte[] evidenceDigest;
                //compute the digest of evidence tree
                if(evidenceDirectoryPath.isDirectory()){
                    ArrayList<File> fileList = UtilsUsable.getListFiles(evidenceDirectoryPath);
                    ArrayList<String> txList = new ArrayList<>();
                    int fileNum = fileList.size();
                    for(int i=0;i<fileNum;i++){
                        txList.add(Sha256Hash.of(fileList.get(i)).toString());
                    }
                    Collections.sort(txList);
                    MerkleTrees merkleTrees = new MerkleTrees(txList);
                    merkleTrees.merkle_tree();
                    String ShaOfEvidence = merkleTrees.getRoot();
                    evidenceDigest = Sha256Hash.wrap(ShaOfEvidence).getBytes();
                } else {
                    evidenceDigest = Sha256Hash.of(evidenceDirectoryPath).getBytes();
                }
                //compute the combined evidence digest
                byte[] illustrateDigest = Sha256Hash.of(evidenceIllFilePath).getBytes();
                byte[] last = new byte[illustrateDigest.length + evidenceDigest.length];
                System.arraycopy(evidenceDigest,0,last,0,evidenceDigest.length);
                System.arraycopy(illustrateDigest,0,last,evidenceDigest.length,illustrateDigest.length);
                Sha256Hash upDigest = Sha256Hash.of(last);
                ForensicsRequest forensicsRequest = new ForensicsRequest();
                tempFinalDigest = upDigest.toString();
                forensicsRequest.setEviHash(tempFinalDigest);
                forensicsRequest.setLightService(false);
                ch.writeAndFlush(forensicsRequest);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            GuiUtils.informationalAlert("warning","Bad path detected!");
        }
    }

    public void forHandleCancel(ActionEvent actionEvent) {
        evidenceDirectoryField.setText("");
        evidenceIllustrateField.setText("");
        resultArea.setText("");
    }



    public void forHandleAudit(ActionEvent actionEvent) {
        if(auditResultDirectoryField.getText() == null){
            GuiUtils.informationalAlert("warning","Lack audit result file." + "\nPlease complete the upload process first!");
            return;
        }
        File auditDirectoryPath = new File(auditResultDirectoryField.getText());
        if(auditDirectoryPath.exists()){
            try {
                String authDigest = null;
                //compute the digest of evidence tree
                if(auditDirectoryPath.isDirectory()){
                    ArrayList<File> fileList = UtilsUsable.getListFiles(auditDirectoryPath);
                    ArrayList<String> txList = new ArrayList<>();
                    int fileNum = fileList.size();
                    for(int i=0;i<fileNum;i++){
                        txList.add(Sha256Hash.of(fileList.get(i)).toString());
                    }
                    Collections.sort(txList);
                    MerkleTrees merkleTrees = new MerkleTrees(txList);
                    merkleTrees.merkle_tree();
                    authDigest = merkleTrees.getRoot();
                } else {
                    authDigest = Sha256Hash.of(auditDirectoryPath).toString();
                }
                String evidenceSelected = (String)evidenceChoiceBox.getValue();
                EasyEvi easyEvi = institution.getEasyEvi(evidenceSelected);
                AuditAns auditAns = new AuditAns();
                auditAns.setFinalDigest(easyEvi.getFinalDigest());
                auditAns.setAuditDigest(authDigest);
                String auditAddress = (String)addressChoiceBox.getValue();
                System.out.println(auditAddress);
                auditAns.setAuditAddress(auditAddress);
                List<ECKey> authKeyList = bitcoin.wallet().getImportedKeys();
                ECKey signer = null;
                for (ECKey ecKey:authKeyList){
                    if(ecKey.getPublicKeyAsHex().equals(auditAddress)){
                        signer = ecKey;
                        break;
                    }
                }
                Transaction tx = new Transaction(bitcoin.params());
                tx.setLockTime(EvidenceOperatorCode.Authenticity.ordinal());
                tx.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(signer.sign(Sha256Hash.wrap(authDigest)).encodeToDER()));
                tx.addOutput(Coin.valueOf(5000),ECKey.fromPublicOnly(Hex.decode(easyEvi.getAddressBounded())));
                SendRequest sendRequest = SendRequest.forTx(tx);
                try {
                    bitcoin.wallet().sendCoins(sendRequest);
                }catch (Exception e){
                    e.printStackTrace();
                }
                auditAns.setTxHash(tx.getHashAsString());
                institution.auditAdd(easyEvi.getFinalDigest(),auditAns);

                AuthNote authNote = new AuthNote();
                authNote.setEcPbkey(auditAddress);
                authNote.setAuthTx(tx.bitcoinSerialize());
                authNote.setFinalDigest(easyEvi.getFinalDigest());
                authNote.setAuthDigest(authDigest);
                ch.writeAndFlush(authNote);

                bitcoin.peerGroup().addBlocksDownloadedEventListener(new BlocksDownloadedEventListener() {
                    @Override
                    public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                        List<Sha256Hash> hashes = new ArrayList<>();
                        PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
                        tree.getTxnHashAndMerkleRoot(hashes);
                        if (hashes.contains(tx.getHash())) {
                            Date timeStamp = filteredBlock.getBlockHeader().getTime();
                            AuditAns auditAnsth = institution.getAuditAnsHashMap().get(easyEvi.getFinalDigest());
                            auditAnsth.setTimestamp(timeStamp);
                            institution.getAuditAnsHashMap().put(easyEvi.getFinalDigest(),auditAnsth);
                            //remove the listener
                            Main.bitcoin.peerGroup().removeBlocksDownloadedEventListener(this);
                        }
                    }
                });

                tx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                    @Override
                    public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                        if(confidence.getConfidenceType()== TransactionConfidence.ConfidenceType.DEAD){
                            System.out.println("Double Spend Detect,txhash Verifies!");
                            Transaction doubleSpendTx = confidence.getOverridingTransaction();
                            AuditAns auditAnsth = institution.getAuditAnsHashMap().get(easyEvi.getFinalDigest());
                            auditAnsth.setTxHash(doubleSpendTx.getHash().toString());
                            auditAnsth.setTimestamp(doubleSpendTx.getUpdateTime());
                            institution.getAuditAnsHashMap().put(easyEvi.getFinalDigest(),auditAnsth);
                            doubleSpendTx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                                @Override
                                public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                                    if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                        return;
                                    if (confidence.getDepthInBlocks() == 3) {
                                        AuditAns auditAnsth = institution.getAuditAnsHashMap().get(easyEvi.getFinalDigest());
                                        auditAnsth.setConfirmed(true);
                                        institution.getAuditAnsHashMap().put(easyEvi.getFinalDigest(),auditAnsth);
                                        doubleSpendTx.getConfidence().removeEventListener(this);
                                    }
                                }
                            });
                            tx.getConfidence().removeEventListener(this);
                        }else {
                            if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                return;
                            if (confidence.getDepthInBlocks() == 3) {
                                AuditAns auditAnsth = institution.getAuditAnsHashMap().get(easyEvi.getFinalDigest());
                                auditAnsth.setConfirmed(true);
                                institution.getAuditAnsHashMap().put(easyEvi.getFinalDigest(),auditAnsth);
                                tx.getConfidence().removeEventListener(this);
                            }
                        }
                    }
                });

            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            GuiUtils.informationalAlert("warning","Bad path detected!");
        }
    }

    public void forHandleAuditCancel(ActionEvent actionEvent) {
        auditResultDirectoryField.setText("");
    }

    public void onUpForAuthDirectory(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Upload the audit result directory:");
        File file = directoryChooser.showDialog(instance.mainWindow);
        if(file == null){
            return;
        }
        auditResultDirectoryField.setText(file.getAbsolutePath());
    }

    public Institution getInstitution() {
        return institution;
    }


    class LoginService extends Service<Channel> {
        private String institutionName;
        private String password;
        public LoginService(String institutionName, String password){
            this.institutionName = institutionName;
            this.password = password;
        }
        @Override
        protected Task<Channel> createTask(){
            return new Task<Channel>() {
                @Override
                protected Channel call() throws Exception {
                    EventLoopGroup group = new NioEventLoopGroup();
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>(){
                                @Override
                                protected void initChannel (SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    ch.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                                    ch.pipeline().addLast(new ObjectEncoder());
                                    pipeline.addLast("handler", new ClientHandler(overWriteMsg));
                                }
                            });
                    Channel ch = b.connect(host, port).sync().channel();
                    Authlog log = new Authlog();
                    User user = new User(institutionName);
                    log.setEmail(user);
                    log.setPassword(password);
                    ch.writeAndFlush(log);
                    return ch;
                }
            };

        }
    }


}
