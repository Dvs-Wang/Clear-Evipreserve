package sample.Controller;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.TestNet3Params;
import sample.Controller.WebController.ClientHandler;
import sample.Main;
import sample.Method.MerkleTrees;
import sample.Method.UtilsUsable;
import sample.Module.Evidence;
import sample.Module.EvidenceChain;
import sample.Module.Share.AuditAnswer;
import sample.Module.Share.Massage.*;
import sample.Module.Share.Proof;
import sample.Module.User;
import sample.Module.UserWrapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.bitcoinj.core.ECKey.fromPublicOnly;

/**
 * Created by WangMingming on 2017/3/3.
 */
public class RootInterfaceController {


    public Button testBtn;
    private Main main;
    public static String host = "127.0.0.1";
    public static int port = 7878;
    private ObjectProperty<Message> overWriteMsg = new ReadOnlyObjectWrapper<Message>();
    private EventLoopGroup group;
    @FXML
    private Accordion mainAccordion;
    //Control items in TitlePane 1
    @FXML
    private TitledPane UYED;
    @FXML
    private ChoiceBox digestObtainChoice;
    @FXML
    private Label notation;
    @FXML
    private TextField digestInput;
    @FXML
    private Group firstGroup;
    @FXML
    private Group secondGroup;
    @FXML
    private Button consummate;
    @FXML
    private Label downLabel;
    @FXML
    private Button fileChooser;
    @FXML
    private Label secondStepLabel;
    @FXML
    private Label lastStepLabel;
    @FXML
    private Group lastGroup;
    @FXML
    private Button turnOverBtn;
    //Control items in TitlePane 2
    @FXML
    private Group loginGroup;
    @FXML
    private TitledPane LR;
    @FXML
    private ChoiceBox LORR;
    @FXML
    private Group regGroup;
    @FXML
    private Label directionLabel;
    @FXML
    private Button applyBt;
    @FXML
    private Button cancelBt;
    @FXML
    private TextField emailLogTextField;
    @FXML
    private PasswordField passwordLogTextField;
    @FXML
    private TextField validateCodeLogTextField;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Label decorateLabel;
    @FXML
    private Label validCodeErrorLabel;
    @FXML
    private HBox buttonBox;
    //Control items in TitlePane 3
    @FXML
    private Label eckeyLb;
    @FXML
    private Label priUser;
    @FXML
    private TitledPane EMT;
    @FXML
    private Text welcome;
    @FXML
    private TableView<Evidence> eviTable;
    @FXML
    private TableColumn<Evidence,String> evidenceNameColume;
    @FXML
    private TableColumn<Evidence,String> evidenceDigestColume;
    @FXML
    private TableColumn<Evidence,Boolean> isUploadColume;
    @FXML
    private TableColumn<Evidence,Boolean> isConfirmedColume;
    public Label cpLabel;
    //Control items in TitlePane 4
    @FXML
    private TitledPane FOC;
    public TextField evidenceDirectoryField;
    public Label evidenceDirectoryBtnLabel;
    public TextField evidenceIllustrateField;
    public Label evidenceIiiustrateBtnLabel;
    public TextArea resultArea;
    public RadioButton distinctServiceCkBox;
    public RadioButton lightWeightServiceCkBox;
    final ToggleGroup toggleGroup = new ToggleGroup();

    private Evidence priEvidence;

    private User userOnline = new User();
    private ObservableList<Evidence> eviData = FXCollections.observableArrayList();
    private int digestObtainChoiceNum = 0;
    private int lorrchoiceNum = 0;
    private Number tableChoiceIndex;
    private boolean connectFlag = false;
    private boolean validateMode = false;
    //represent whether the user has login in!
    private boolean isLogin = false;
    //represent whether an offline evidence exists
    private boolean evidenceOfflineExisted = false;
    private Sha256Hash hashOfEvidence;
    private Sha256Hash finalHashOfEvidence;
    private Sha256Hash upDigest;
    public Channel ch;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        testBtn.setVisible(false);
        mainAccordion.setExpandedPane(UYED);
        EMT.setDisable(true);
        FOC.setDisable(true);
        regGroup.setDisable(true);
        Image icon12 = new Image(getClass().getClassLoader().getResourceAsStream("View/Resources/ico/f7.png"));
        ImageView imageView = new ImageView(icon12);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        fileChooser.setGraphic(imageView);
        AwesomeDude.setIcon(downLabel,AwesomeIcon.DOWNLOAD);
        downLabel.setStyle("-fx-font-family: FontAwesome; -fx-font-size: 30;");
        decorateLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("View/Resources/timg.png"))));
        // Initialize the choiceBox.
        digestObtainChoice.setItems(FXCollections.observableArrayList("Submit The Digest of Evidence","I don't have the digest!"));
        digestObtainChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                digestObtainChoiceNum = newValue.intValue();
                setNotation();
            }
        });
        LORR.setItems(FXCollections.observableArrayList("LOGIN","REGISTER(CASE YOU DON'T HAVE AN ACCOUNT)"));
        LORR.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if((lorrchoiceNum = newValue.intValue()) == 0){
                    directionLabel.setText("Login");
                }else {
                    directionLabel.setText("Register");
                }
            }
        });
        setFOC();

    }

    public void setNotation(){
        if(digestObtainChoiceNum == 0){
            notation.setText("Notation: Paste Your Evidence Digest to the Textfield aside. " +
                    "Make sure the format is Sha256 in hex!");
        }else {
            notation.setText("Notation: Input the Path of Your Evidence File and we will help" +
                    " you make the Digest");
        }
    }

    public void setMain(Main main){
        this.main = main;
    }

    @FXML
    public void handleReChoose(){
        digestInput.setText("");
    }

    @FXML
    public void handleSubmit(){
        if(digestObtainChoiceNum == 0){
            if(checkInputSha256()){
                hashOfEvidence = Sha256Hash.wrap(digestInput.getText());
                notation.setText("Ok!" + " The digest is " + hashOfEvidence);
                main.setHashOfEvidence(hashOfEvidence);
                firstGroup.setDisable(true);
                secondGroup.setVisible(true);
                secondGroup.setDisable(false);
            }else {
                notation.setText("Warning: Abnormal Input Digest Format !" +
                        " Make sure the format is Sha256 in hex");
            }
        }else {
                String url = digestInput.getText();
                int evidenceFileNum = 0;
                try{
                    File file = new File(url);
                    if(file.isDirectory()){
                        ArrayList<File> fileList = UtilsUsable.getListFiles(file);
                        ArrayList<String> txList = new ArrayList<>();
                        int fileNum = fileList.size();
                        for(int i=0;i<fileNum;i++){
                                txList.add(Sha256Hash.of(fileList.get(i)).toString());
                                evidenceFileNum ++ ;
                        }
                        Collections.sort(txList);
                        MerkleTrees merkleTrees = new MerkleTrees(txList);
                        merkleTrees.merkle_tree();
                        String ShaOfEvidence = merkleTrees.getRoot();
                        hashOfEvidence = Sha256Hash.wrap(ShaOfEvidence);
                    } else {
                        hashOfEvidence = Sha256Hash.of(file);
                        evidenceFileNum = 1;
                    }
                    notation.setText("Ok!" + " The digest is " + hashOfEvidence + "  Evidence file number: " + evidenceFileNum);
                    main.setHashOfEvidence(hashOfEvidence);
                    firstGroup.setDisable(true);
                    secondGroup.setVisible(true);
                    secondGroup.setDisable(false);
                }catch (IOException e){
                    notation.setText("Warning: Invalid path for the Evidence!");
                }
        }

    }

    /**
     * check whether the input form suits standard Sha256
     * @return
     */
    private boolean checkInputSha256(){
        String checkStr = digestInput.getText();
        boolean invalidFlag = false;
        byte[] b2c = checkStr.toLowerCase().getBytes();
        for(byte i:b2c){
            if((i>='0'&&i<='9')||(i>='a'&&i<='z')){
                continue;
            }else {
                invalidFlag = true;
                break;
            }
        }
        if (checkStr.length() != 64){
            return false;
        }else if(invalidFlag){
            return false;
        }else {
            return true;
        }
    }

    @FXML
    private void handleConsummate(){
        main.showNoticePage();
        if(main.getFileNum() != 0){
            consummate.setDisable(true);
            downLabel.setDisable(false);
        }
    }

    @FXML
    private void handleDownload(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the path to save the assistant file:");
        File directory = directoryChooser.showDialog(main.getPrimaryStage());
        if (directory != null){
            try{
                File readFrom = new File("src/main/java/sample/Data/" + main.getFileNum() + ".evidence");
                File writeTo = new File(directory.getAbsolutePath() + "\\" + main.getFileNum() + ".evidence");
                if(!writeTo.exists()){
                    writeTo.createNewFile();
                }
                UtilsUsable.customBufferBufferedStreamCopy(readFrom,writeTo);
                secondStepLabel.setText("File has been sucessfully extracted ! Please check and preserve it carefully! ");
                //compute the final hash of evidence.
                finalHashOfEvidence = Sha256Hash.of(readFrom);
                byte[] mid = finalHashOfEvidence.getBytes();
                byte[] raw = hashOfEvidence.getBytes();
                byte[] last = new byte[mid.length + raw.length];
                System.arraycopy(raw,0,last,0,raw.length);
                System.arraycopy(mid,0,last,raw.length,mid.length);
                finalHashOfEvidence = Sha256Hash.of(last);
                lastGroup.setVisible(true);
                lastStepLabel.setText("The final digest (combined the table data) is " + finalHashOfEvidence);
                evidenceOfflineExisted = true;

            }catch (IOException e){
                secondStepLabel.setText("Invalid path! Make sure the input is a valid directory!");
            }

        }

    }

    @FXML
    private void handleLogCancle(){
        if(!validateMode){
            emailLogTextField.setText("");
            passwordLogTextField.setText("");
        }
        validateCodeLogTextField.setText("");
    }

    public static void alertForSth(String str){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(str);
        alert.showAndWait();
    }

    @FXML
    private void handleLogApply(){
        //clean the error label
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        validCodeErrorLabel.setText("");
        //main logic
        if(checkLorR()){
            if(!connectFlag){
                boolean Lor = (lorrchoiceNum == 0);
                LorRService lor = new LorRService(emailLogTextField.getText(),passwordLogTextField.getText(),Lor);
                //
                lor.setOnSucceeded(t -> ch = (Channel) t.getSource().getValue());
                lor.start();
                lockState();
                overWriteMsg.addListener((observable, oldValue, newValue) -> {
                    switch (overWriteMsg.get().getType()){
                        case CHECKEMAIL:
                            regGroup.setDisable(false);
                            validateMode = true;
                            cancelBt.setDisable(false);
                            buttonBox.setDisable(false);
                            validateCodeLogTextField.setPromptText("Please check your mail to get the validate code!");
                            break;
                        case RELOGIN:
                            freeState();
                            emailErrorLabel.setText("Email has been registered! Please log in!");
                            break;
                        case WRONGPASSWORD:
                            freeState();
                            passwordErrorLabel.setText("Wrong Password!");
                            break;
                        case PASSPORT:
                            //System.out.println("pass!");
                            isLogin = true;
                            userOnline.setEmail(emailLogTextField.getText());
                            userOnline.setPassword(passwordLogTextField.getText());
                            changeState();
                            break;
                        case NONEUSER:
                            freeState();
                            emailErrorLabel.setText("The email hasn't been regsitered!");
                            break;
                        case REGSUCCESS:
                            //System.out.println("Register Success!" );
                            isLogin = true;
                            String hexEckey = ((RegSuccessMsg)overWriteMsg.get()).getEcKey();
                            userOnline.setEncryptedEckey(hexEckey);
                            userOnline.setEmail(emailLogTextField.getText());
                            userOnline.setPassword(passwordLogTextField.getText());
                            changeState();
                            //System.out.println(mid);
                            break;
                        case WRONGVALI:
                            regGroup.setDisable(false);
                            cancelBt.setDisable(false);
                            buttonBox.setDisable(false);
                            validCodeErrorLabel.setText("Wrong ValidateCode!");
                            break;
                        case UPLOADSUC:
                            if(!((UploadSuccessMsg)overWriteMsg.get()).isLightService()){
                                //alter the evidence with the proof
                                Proof proof = ((UploadSuccessMsg)overWriteMsg.get()).getProof();
                                Evidence evidence = eviData.get(tableChoiceIndex.intValue());
                                evidence.setTxHash(proof.getTxHash());
                                evidence.setECkeyBounded(proof.getECkeyBounded());
                                evidence.setIsUpload(true);
                                eviData.set(tableChoiceIndex.intValue(),evidence);
                                userOnline.handledEvidences.remove(Sha256Hash.wrap(evidence.getFinalDigest()));
                                userOnline.handledEvidences.put(Sha256Hash.wrap(evidence.getFinalDigest()),evidence);
                                main.showEvidenceDetailController.setEvidence(evidence);
                                if(proof.getParentDigest()!= null){
                                    EvidenceChain evidenceChain = searchEvidenceChainExisted(proof.getParentDigest());
                                    if(evidenceChain!=null){
                                        evidenceChain.chainAdd(evidence);
                                    }else {
                                        evidenceChain = new EvidenceChain();
                                        evidenceChain.chainAdd(userOnline.getEvidence(Sha256Hash.wrap(proof.getParentDigest())));
                                        evidenceChain.chainAdd(evidence);

                                    }
                                    userOnline.evidenceChains.add(evidenceChain);
                                }
                                alertForSth("Distinct Update Success!");
                            }else {
                                Evidence evidence = eviData.get(tableChoiceIndex.intValue());
                                evidence.setIsUpload(true);
                                eviData.set(tableChoiceIndex.intValue(),evidence);
                                userOnline.handledEvidences.remove(Sha256Hash.wrap(evidence.getFinalDigest()));
                                userOnline.handledEvidences.put(Sha256Hash.wrap(evidence.getFinalDigest()),evidence);
                                main.showEvidenceDetailController.setEvidence(evidence);
                                alertForSth("Light Update Success!\nPlease wait for the combined digest to update!");
                            }
                            break;
                        case SYNCANS:
                            SyncAnswerMsg syncAnswerMsg = (SyncAnswerMsg)overWriteMsg.get();
                            if(!syncAnswerMsg.isLightService()){
                                Proof syncProof = syncAnswerMsg.getProof();
                                Evidence evidenceSyn = eviData.get(tableChoiceIndex.intValue());
                                if(syncAnswerMsg.getAuditAnswer()!=null){
                                    evidenceSyn.setAuditAns(syncAnswerMsg.getAuditAnswer());
                                }
                                evidenceSyn.setTxHash(syncProof.getTxHash());
                                if(syncProof.getECkeyBounded()!=null){
                                    evidenceSyn.setECkeyBounded(syncProof.getECkeyBounded());
                                }
                                if(syncProof.getConfirmTimes()>=3){
                                    evidenceSyn.setIsConfirmed(true);
                                }
                                evidenceSyn.setConfirmTimes(syncProof.getConfirmTimes());
                                if(syncProof.getTimestamp()!=null){
                                    evidenceSyn.setTimeStamp(syncProof.getTimestamp());
                                }
                                eviData.set(tableChoiceIndex.intValue(),evidenceSyn);
                                userOnline.handledEvidences.remove(Sha256Hash.wrap(evidenceSyn.getFinalDigest()));
                                userOnline.handledEvidences.put(Sha256Hash.wrap(evidenceSyn.getFinalDigest()),evidenceSyn);
                                main.showEvidenceDetailController.setEvidence(evidenceSyn);
                                alertForSth("Sync Success!");
                            }else {
                                if(!syncAnswerMsg.isProofComplete()){
                                    alertForSth("Sync Success! \nLight service for current digest is still in progress, please wait for successive development!");
                                }else {
                                    alertForSth("Sync Success! \nLight service for current digest proof receives,please check it!");
                                    Proof proof = syncAnswerMsg.getProof();
                                    Evidence evidenceSyn = eviData.get(tableChoiceIndex.intValue());
                                    evidenceSyn.setLightProof(proof);
                                    if(proof.getConfirmTimes()>=3){
                                        evidenceSyn.setIsConfirmed(true);
                                    }
                                    eviData.set(tableChoiceIndex.intValue(),evidenceSyn);
                                    userOnline.handledEvidences.remove(Sha256Hash.wrap(evidenceSyn.getFinalDigest()));
                                    userOnline.handledEvidences.put(Sha256Hash.wrap(evidenceSyn.getFinalDigest()),evidenceSyn);
                                    main.showEvidenceDetailController.setEvidence(evidenceSyn);
                                }
                            }

                            break;
                        case FORANS:
                            ForensicsAns forensicsAns = (ForensicsAns)overWriteMsg.get();
                            if(forensicsAns.isExisted()){
                                if(!forensicsAns.isComplete()){
                                    resultArea.setText("Light service detected on current evidence but not complete, more time to wait for further information!");
                                }else {
                                    if(forensicsAns.getTimestamp() == null){
                                        resultArea.setText("Digest Detected on the Bitcoin Blockchain, but not been received by block yet please wait for the blockchain confirmation!(Normally less than half an hour from the evidence uploaded)");
                                    }else {
                                        resultArea.setText("Digest Detected on the Bitcoin Blockchain in tx: " + forensicsAns.getTxHash() + ".\nIntegrity and Timeliness proved before " + forensicsAns.getTimestamp()+"\n");
                                        if(forensicsAns.isConfirmed()){
                                            String st = resultArea.getText();
                                            resultArea.setText(st + "Digest has been witnessed by more than 3+ blocks and it's seen as Non-tempered Evidence!");
                                        }else {
                                            String st = resultArea.getText();
                                            resultArea.setText(st + "Digest has been witnessed by less than 3 blocks and need to wait for more confirmations!");
                                        }
                                        String st = resultArea.getText();
                                        resultArea.setText(st + "\nSafe link to view the eivdence on the blockchain: "+"\nhttps://live.blockcypher.com/btc-testnet/tx/" + forensicsAns.getTxHash());

                                    }
                                    if(forensicsAns.getSeedDigest() != null){
                                        String st = resultArea.getText();
                                        resultArea.setText(st + "\nThe service type is lightweight, connected seed digest shows as follows:\n" + forensicsAns.getSeedDigest());
                                    }
                                }

                            }else {
                                resultArea.setText("No Such Digest existed on Bitcoin Blockchain!");
                            }

                            break;
                        default: break;
                    }
                });

                connectFlag = true;

            }else {
                if(checkLorR()){
                    if(lorrchoiceNum == 0){
                        LoginMsg log = new LoginMsg();
                        log.setPassword(passwordLogTextField.getText());
                        log.setEmail(new User(emailLogTextField.getText()));
                        ch.writeAndFlush(log);
                    }else {
                        if(validateMode){
                            ValidateMsg vali = new ValidateMsg();
                            vali.setEmail(new User(emailLogTextField.getText()));
                            vali.setPassword(passwordLogTextField.getText());
                            vali.setValidateCode(validateCodeLogTextField.getText());
                            ch.writeAndFlush(vali);
                        }else {
                            RegisterMsg reg = new RegisterMsg();
                            reg.setPassword(passwordLogTextField.getText());
                            reg.setEmail(new User(emailLogTextField.getText()));
                            ch.writeAndFlush(reg);
                        }
                    }
                }
                lockState();
            }
        }

    }

    public  EvidenceChain searchEvidenceChainExisted(String digest){
        Iterator<EvidenceChain> iterator = userOnline.evidenceChains.iterator();
        while (iterator.hasNext()){
            EvidenceChain evidenceChain = iterator.next();
            if(evidenceChain.searchChainOfDigest(digest)){
                return evidenceChain;
            }
        }
        return null;
    }

    /**
     *change the program state when successful login action happens
     * sync the data from the server and local files
     */
    private void changeState(){
        //If an evidence has already been accepted, connect it to the user
        if(evidenceOfflineExisted){
            priEvidence = main.getPriEvidence();
            priEvidence.setFinalDigest(finalHashOfEvidence.toString());
            userOnline.handledEvidences.put(finalHashOfEvidence,priEvidence);
            eviData.add(priEvidence);
            finalHashOfEvidence = null;
            priEvidence = null;
            cleanStep1();
        }
        //activate the eviTable
        load();
        eviTable.setItems(eviData);
        evidenceNameColume.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        evidenceDigestColume.setCellValueFactory(cellData -> cellData.getValue().finalDigestProperty());
        isUploadColume.setCellValueFactory(cellData -> cellData.getValue().isUploadProperty());
        isConfirmedColume.setCellValueFactory(cellData -> cellData.getValue().isConfirmedPorperty());
        eviTable.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> tableChoiceIndex = newValue));
        LR.setDisable(true);
        eviTable.setDisable(false);
        EMT.setDisable(false);
        FOC.setDisable(false);
        setText();
        mainAccordion.setExpandedPane(EMT);
    }

    private void cleanStep1(){
        main.setFileNum(0);
        digestInput.setText("");
        notation.setText("Notation:");
        firstGroup.setDisable(false);
        consummate.setDisable(false);
        downLabel.setDisable(true);
        secondStepLabel.setText("");
        secondGroup.setVisible(false);
        lastStepLabel.setText("");
        lastGroup.setVisible(false);
    }

    /**
     * lock the connected button and label state when log/register communication happens for security purpose
     */
    private void lockState(){
        loginGroup.setDisable(true);
        regGroup.setDisable(true);
        LORR.setDisable(true);
        buttonBox.setDisable(true);
    }

    /**
     * unlock the state when communication is over
     */
    private void freeState(){
        loginGroup.setDisable(false);
        LORR.setDisable(false);
        buttonBox.setDisable(false);
    }

    private void setText(){
        InnerShadow is = new InnerShadow();
        is.setOffsetX(1.0f);
        is.setOffsetY(1.0f);
        welcome.setEffect(is);
        welcome.setText("Welcome");
        welcome.setFill(Color.YELLOW);
        welcome.setFont(Font.font(null, FontWeight.BOLD, 20));
        priUser.setText("USER:  " + userOnline.getEmail());
        priUser.setFont(Font.font(null, FontWeight.BOLD, 15));
        Tooltip.install(cpLabel,new Tooltip("Click to copy your Address to the dashboard:" + "\n" + userOnline.getEncryptedEckey()));
        eckeyLb.setFont(Font.font(null, FontWeight.BOLD, 15));
        /*
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream("View/Resources/ico/b6.png")));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);*/
        cpLabel.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(userOnline.getEncryptedEckey());
                clipboard.setContent(content);
            }
        });
        AwesomeDude.setIcon(cpLabel,AwesomeIcon.COPY);
    }

    private boolean checkLorR(){
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*\\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))^.{8,16}$");
        if (!UtilsUsable.checkEmail(emailLogTextField.getText())){
            emailErrorLabel.setText("Invalid Email Format!");
            return false;
        }else if(!pattern.matcher(passwordLogTextField.getText()).matches()){
            passwordLogTextField.setPromptText("Length 8-12 bits, numbers and characters contained!");
            passwordErrorLabel.setText("Invalid Password Format!");
            return false;
        }else if(validateMode){
            if(validateCodeLogTextField.getText() == null || validateCodeLogTextField.getText().length()==0){
                validCodeErrorLabel.setText("Validate Code should not be null!");
                return false;
            }else {
                return true;
            }
        } else {
            return true;
        }
    }
    @FXML
    private void handleNewEvi() {
        //clear the state of Step1
        cleanStep1();
        turnOverBtn.setText("Add it!");
        //turn to the Step1
        mainAccordion.setExpandedPane(UYED);
    }

    /**
     * delete an evidence which hasn't been uploaded
     */
    @FXML
    private void handleDelEvi() {
        //check if a evi has been chosen
        if(tableChoiceIndex != null && tableChoiceIndex.intValue() != -1){
        //check if the evi can be deleted, if not, show the warning
            Evidence priChoiceEvi = eviData.get(tableChoiceIndex.intValue());
            if(!priChoiceEvi.getIsUpload()){
                //delete the evidence info
                eviData.remove(tableChoiceIndex.intValue());
                userOnline.handledEvidences.remove(Sha256Hash.wrap(priChoiceEvi.getFinalDigest()));
            }else {
                showWarning("Evidence has been uploaded and can't be deleted!");
            }

        }else {
            showWarning("None evidence has been chosen!");
        }
    }

    private static void showWarning(String warning){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(warning);
        alert.showAndWait();
    }


    public void handleShowDetails() {
        //check if a evi has been chosen
        if(tableChoiceIndex != null && tableChoiceIndex.intValue() != -1){
            //transform the window
            main.setPriEvidence(eviData.get(tableChoiceIndex.intValue()));
            main.showEvidenceDetailPage();
        }else {
            showWarning("None evidence has been chosen!");
        }
    }

    /**
     * handle the jump from step 1 to other steps
     */
    @FXML
    private void onClickTurnOver() {
        //check the whether login happens
        if(!isLogin){
            //if false switch to step 2
            mainAccordion.setExpandedPane(LR);
        }else {
            //if true save the evi to the userData and switch to step 3
            priEvidence = main.getPriEvidence();
            priEvidence.setFinalDigest(finalHashOfEvidence.toString());
            userOnline.handledEvidences.put(finalHashOfEvidence,priEvidence);
            eviData.add(priEvidence);
            finalHashOfEvidence = null;
            priEvidence = null;
            cleanStep1();
            mainAccordion.setExpandedPane(EMT);
            EMT.setDisable(false);
        }
    }
    /**
     *btn for per step test
     */
    public void handleTestBtn(ActionEvent actionEvent) {
        userOnline.setEmail("wsd123456a@163.com");
        userOnline.setPassword("qwer1234");
        isLogin = true;
        changeState();
    }

    /**
     * save the present user state to a xml file,
     * xml name: Sha256of(email) + ".xml",
     * store: 1. Eckey encrypted by password
     *        2. Evidence state:
     *              final digest + an encrypted evidence file(decrypted by user's password)
     */
    public void save() {
        if(userOnline.getSaltAddedPassword() == null){
            return;
        }
        //fresh the file that will be used to save the user state
        try {
            File file = new File("src/main/java/sample/Data/" + Sha256Hash.of(userOnline.getEmail().getBytes()).toString() + ".xml");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        //init the Jaxb marshaller
            JAXBContext context = JAXBContext.newInstance(UserWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        //load the system state to the wrapper class and marshal the class to the xml file
            UserWrapper userWrapper = new UserWrapper();
            userWrapper.UserWrapperInit(userOnline,userOnline.getSaltAddedPassword());
            marshaller.marshal(userWrapper, file);
        }catch (Exception io){
            io.printStackTrace();
        }
    }

    /**
     * load the online user state from the xml file
     */
    public void load(){
        //check if corresponding file exists, if not, do nothing!
        File file = new File("src/main/java/sample/Data/" + Sha256Hash.of(userOnline.getEmail().getBytes()).toString() + ".xml");
        if(file.exists()){
            try{
                //init the Jaxb unmarshaller
                JAXBContext context = JAXBContext.newInstance(UserWrapper.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                //get the wrapper class and recover the data
                UserWrapper userWrapper = (UserWrapper)unmarshaller.unmarshal(file);
                User tempUser = userWrapper.decodeFromUserWrapper(userOnline.getSaltAddedPassword());
                userOnline.setEncryptedEckey(tempUser.getEncryptedEckey());
                userOnline.evidenceChains = tempUser.evidenceChains;
                userOnline.handledEvidences.putAll(tempUser.handledEvidences);
                Iterator<Map.Entry<Sha256Hash, Evidence>> iter = tempUser.handledEvidences.entrySet().iterator();
                while (iter.hasNext()){
                    Map.Entry entry = iter.next();
                    Evidence val = (Evidence) (entry.getValue());
                    eviData.add(val);
                }
            }catch (Exception oi){
                oi.printStackTrace();
            }
        }
    }

    public void handleFileChooser(ActionEvent actionEvent) {
        if(digestObtainChoiceNum == 1){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select your evidence file:");
            File doc = directoryChooser.showDialog(main.getPrimaryStage());
            if(doc == null){
                return;
            }
            digestInput.setText(doc.getAbsolutePath());
        }
    }

    /**
     * forensics submit method
     * @param actionEvent
     */
    public void forHandleUp(ActionEvent actionEvent) {
        if(evidenceDirectoryField.getText() == null || evidenceIllustrateField.getText()==null){
            showWarning("Lack evidence file." + "\nPlease complete the upload process first!");
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
                forensicsRequest.setEviHash(upDigest.toString());
                forensicsRequest.setLightService(lightWeightServiceCkBox.isSelected());
                ch.writeAndFlush(forensicsRequest);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            showWarning("Bad path detected!");
        }
    }

    public void forHandleCancel(ActionEvent actionEvent) {
        evidenceDirectoryField.setText("");
        evidenceIllustrateField.setText("");
        resultArea.setText("");
    }

    public void onUpForDirectory(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Upload your evidence directory:");
        File file = directoryChooser.showDialog(main.getPrimaryStage());
        if(file == null){
            return;
        }
        evidenceDirectoryField.setText(file.getAbsolutePath());
    }

    public void onUpForFile(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your evidence illustrate file:");
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if(file == null){
            return;
        }
        evidenceIllustrateField.setText(file.getAbsolutePath());
    }

    class LorRService extends Service<Channel> {
        private String email;
        private String password;
        private boolean Lor;
        public LorRService(String email, String password, boolean Lor){
            this.email = email;
            this.password = password;
            this.Lor = Lor;

        }
        @Override
        protected Task<Channel> createTask(){
            return new Task<Channel>() {
                @Override
                protected Channel call() throws Exception {
                    group = new NioEventLoopGroup();
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
                    if(Lor){
                        LoginMsg log = new LoginMsg();
                        User user = new User(email);
                        log.setEmail(user);
                        log.setPassword(password);
                        ch.writeAndFlush(log);
                        return ch;
                    }else {
                        RegisterMsg reg = new RegisterMsg();
                        User user = new User(email);
                        reg.setEmail(user);
                        reg.setPassword(password);
                        ch.writeAndFlush(reg);
                        return ch;
                    }

                }
            };

        }
    }


    /**
     * init the view in FOC part
     */
    private void setFOC(){
        distinctServiceCkBox.setToggleGroup(toggleGroup);
        lightWeightServiceCkBox.setToggleGroup(toggleGroup);
        evidenceDirectoryField.setPromptText("Upload your Evidence Directory Path Here:");
        evidenceIllustrateField.setPromptText("Upload your Evidence Illustrate File Path Here:");
        AwesomeDude.setIcon(evidenceDirectoryBtnLabel, AwesomeIcon.UPLOAD);
        AwesomeDude.setIcon(evidenceIiiustrateBtnLabel, AwesomeIcon.UPLOAD);
        Tooltip.install(evidenceDirectoryBtnLabel,new Tooltip("Click to upload your evidence directory!"));
        Tooltip.install(evidenceIiiustrateBtnLabel,new Tooltip("Click to upload yout evidence illustrate file!"));
        resultArea.setPromptText("The result will be shown here!");
        resultArea.setPrefColumnCount(15);
    }

    /**
     * catch the parent evidence which fit to build to a evidence chain
     * @return return a hash map which maps the digest and the evidence name
     */
    public HashMap<String,String> catchChainOnList(){
        HashMap<String,String> returnMap = new HashMap<>();
        for(Evidence evidence:eviData){
            if( evidence.getIsUpload() && evidence.getTxHash()!=null && evidence.getTxHash().length() != 0 && evidence.getConfirmTimes()>=1){
               returnMap.put(evidence.getFinalDigest(),evidence.getName());
            }
        }
        return  returnMap;
    }

    public EventLoopGroup getEventLoopGroup(){
        return this.group;
    }

    public ObjectProperty<Message> getOverWriteMsg(){
        return overWriteMsg;
    }

    public User getUserOnline() {
        return userOnline;
    }

    public void getController(){}

}
