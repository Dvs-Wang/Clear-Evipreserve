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

package wallettemplate;

import Method.MerkleTrees;
import Method.UtilsUsable;
import com.google.common.collect.Lists;
import com.sun.deploy.util.StringUtils;
import com.wang.serverDb.*;
import com.wang.serverDb.Evidence;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Duration;
import org.bitcoinj.core.*;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.signers.MissingSigResolutionSigner;
import org.bitcoinj.signers.TransactionSigner;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.DecryptingKeyBag;
import org.bitcoinj.wallet.KeyBag;
import org.bitcoinj.wallet.RedeemData;
import org.bitcoinj.wallet.SendRequest;
import org.fxmisc.easybind.EasyBind;
import org.spongycastle.util.encoders.Hex;
import sample.Module.Share.*;
import sample.Module.Share.Massage.*;
import sample.Module.Share.Massage.Message;
import wallettemplate.controls.ClickableBitcoinAddress;
import wallettemplate.controls.NotificationBarPane;
import wallettemplate.utils.BitcoinUIModel;
import wallettemplate.utils.easing.EasingMode;
import wallettemplate.utils.easing.ElasticInterpolator;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static Method.SendMail.processRegister;
import static wallettemplate.Main.bitcoin;


/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
public class MainController {
    public HBox controlsBox;
    public Label balance;
    public Button sendMoneyOutBtn;
    public ClickableBitcoinAddress addressControl;
    public ListView<Proof> proofList;
    public ConcurrentHashMap<String,Proof> easyRetrievalMap = new ConcurrentHashMap<>();
    public Label fitBtnLabel;
    public TextField Time2WaitField;
    public TextField request2WaitField;
    public Label receivedRequestNumLabel;

    private int requestNum = 0;//the num of requests received by now for the next combined evidence
    private int requestNum2Wait = 5;//request num to wait for combined evidence
    private int requestTime2Wait = 60;//request time to wait for combined evidence
    private ConcurrentHashMap<String,String> currentSeedDigest = new ConcurrentHashMap<>();
    private BitcoinUIModel model = new BitcoinUIModel();
    private NotificationBarPane.Item syncItem;
    private ServerDao serverDao = new ServerDao();
    private Timer timer = new Timer();


    // Called by FXMLLoader.
    public void initialize() {
        addressControl.setOpacity(0.0);
        AwesomeDude.setIcon(fitBtnLabel, AwesomeIcon.CHECK);
        Tooltip.install(fitBtnLabel,new Tooltip("click to save the setting on combined evidence service!"));
        //init timer for combined evidence service
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                broadCastCombinedEvidence();
            }
        },60000*requestTime2Wait);
        proofList.setCellFactory(new Callback<ListView<Proof>, ListCell<Proof>>() {
            @Override
            public ListCell<Proof> call(ListView<Proof> param) {
                return new ListCell<Proof>() {
                    @Override
                    protected void updateItem(Proof item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText("");
                            setGraphic(null);
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setText("Proof for " + item.getEviSimpleInfo());
                                    ProgressBar bar = new ProgressBar();
                                    bar.progressProperty().bind(item.depthProperty().divide(3.0));
                                    setGraphic(bar);
                                }
                            });

                        }
                    }
                };
            }
        });
        WebDatabaseService webDatabaseService = new WebDatabaseService();
        webDatabaseService.start();
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
    /**
     *
     * @param finalDigest
     * @param userECkey
     */
    public Proof broadCastEvidence(String email,String finalDigest,ECKey userECkey,String parentTxHash) throws Exception{
        //build the tx
        Transaction tx = new Transaction(Main.params);
        tx.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(Hex.decode(finalDigest)));
        tx.addOutput(Coin.valueOf(5000),userECkey);
        if(parentTxHash != null){
            tx.setLockTime(EvidenceOperatorCode.Update.ordinal());
            Sha256Hash txhash = Sha256Hash.wrap(parentTxHash);
            Transaction parentTx = bitcoin.wallet().getTransaction(txhash);
            SendRequest sendRequest = SendRequest.forTx(tx);
            bitcoin.wallet().completeTx(sendRequest);
            TransactionOutput availOut = getAvailIdex(parentTx);
            tx.addInput(availOut);
            Sha256Hash hashForTxTest = tx.hashForSignature(1,availOut.getScriptPubKey(), Transaction.SigHash.ALL,false);
            ECKey.ECDSASignature ecdsaSignature = userECkey.sign(hashForTxTest);
            Script inputScript = ScriptBuilder.createInputScript(new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL,false));
            tx.getInput(1).setScriptSig(inputScript);
            sendRequest = SendRequest.forTx(tx);
            TransactionInput txIn = tx.getInput(0);
            List<TransactionSigner> signers = bitcoin.wallet().getTransactionSigners();
            KeyBag maybeDecryptingKeyBag = new DecryptingKeyBag(bitcoin.wallet(), sendRequest.aesKey);
            Script scriptPubKey = txIn.getConnectedOutput().getScriptPubKey();
            RedeemData redeemData = txIn.getConnectedRedeemData(maybeDecryptingKeyBag);
            txIn.setScriptSig(scriptPubKey.createEmptyInputScript(redeemData.keys.get(0), redeemData.redeemScript));
            TransactionSigner.ProposedTransaction proposal = new TransactionSigner.ProposedTransaction(tx);
            for (TransactionSigner signer : signers) {
                if (!signer.signInputs(proposal, maybeDecryptingKeyBag))
                    System.out.println("warning! Signing Error Existed!");
            }
            new MissingSigResolutionSigner(sendRequest.missingSigsMode).signInputs(proposal, maybeDecryptingKeyBag);
            System.out.println(tx);
            bitcoin.peerGroup().broadcastTransaction(tx);
            bitcoin.wallet().commitTx(tx);
        }else {
            tx.setLockTime(EvidenceOperatorCode.DinstinctEvidence.ordinal());
            Main.bitcoin.wallet().sendCoins(SendRequest.forTx(tx));
        }
        Proof proof = new Proof();
        proof.setEviSimpleInfo(finalDigest);
        proofList.getItems().add(proof);
        proof.setECkeyBounded(userECkey.getPublicKeyAsHex());
        proof.setTxHash(tx.getHashAsString());
        proof.setDepth(0);
        proof.setConfirmTimes(0);
        easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
        Main.bitcoin.peerGroup().addBlocksDownloadedEventListener(new BlocksDownloadedEventListener() {
            @Override
            public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                List<Sha256Hash> hashes = new ArrayList<>();
                PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
                tree.getTxnHashAndMerkleRoot(hashes);
                if (hashes.contains(tx.getHash())) {
                    Date timeStamp = filteredBlock.getBlockHeader().getTime();
                    proof.setTimestamp(timeStamp);
                    Evidence e = serverDao.getEvidenceInfoOnly(finalDigest,email);
                    e.setTxHash(tx.getHash().toString());
                    e.setTimeStamp(timeStamp);
                    serverDao.modifyEvidence(e);
                    easyRetrievalMap.remove(proof.getEviSimpleInfo());
                    easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                    //remove the listener
                    Main.bitcoin.peerGroup().removeBlocksDownloadedEventListener(this);
                }
            }});
        tx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                if(confidence.getConfidenceType() == TransactionConfidence.ConfidenceType.DEAD){
                    Transaction doubleSpendTx = confidence.getOverridingTransaction();
                    proof.setTxHash(doubleSpendTx.getHash().toString());
                    proof.setTimestamp(doubleSpendTx.getUpdateTime());
                    proof.setConfirmTimes(1);
                    proof.setDepth(1);
                    Evidence e = serverDao.getEvidenceInfoOnly(finalDigest,email);
                    e.setTxHash(doubleSpendTx.getHash().toString());
                    e.setTimeStamp(doubleSpendTx.getUpdateTime());
                    serverDao.modifyEvidence(e);
                    easyRetrievalMap.remove(proof.getEviSimpleInfo());
                    easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                    doubleSpendTx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                        @Override
                        public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                            if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                return;
                            proof.setDepth(confidence.getDepthInBlocks());
                            proof.setConfirmTimes(confidence.getDepthInBlocks());
                            easyRetrievalMap.remove(proof.getEviSimpleInfo());
                            easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                            if (proof.getDepth() == 3) {
                                //change the evidence state in the database
                                Evidence e = serverDao.getEvidenceInfoOnly(finalDigest,email);
                                byte b = 1;
                                e.setIsConfirmed(b);
                                serverDao.modifyEvidence(e);
                                //remove the proof from the observing proofList
                                proofList.getItems().remove(proof);
                                //remove the listener
                                doubleSpendTx.getConfidence().removeEventListener(this);
                            }
                        }
                    });
                    tx.getConfidence().removeEventListener(this);
                }else {
                    if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                        return;
                    proof.setDepth(confidence.getDepthInBlocks());
                    proof.setConfirmTimes(confidence.getDepthInBlocks());
                    easyRetrievalMap.remove(proof.getEviSimpleInfo());
                    easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                    if (proof.getDepth() == 3) {
                        //change the evidence state in the database
                        Evidence e = serverDao.getEvidenceInfoOnly(finalDigest,email);
                        byte b = 1;
                        e.setIsConfirmed(b);
                        serverDao.modifyEvidence(e);
                        //remove the proof from the observing proofList
                        proofList.getItems().remove(proof);
                        //remove the listener
                        tx.getConfidence().removeEventListener(this);
                    }
                }
            }
        });
        return proof;
    }

    private TransactionOutput getAvailIdex(Transaction parentTx) {
        List<TransactionOutput> outputList = parentTx.getOutputs();
        for(TransactionOutput i:outputList){
            if(i.getValue().equals(Coin.valueOf(5000))){
                return i;
            }
        }
        return null;
    }

    public Proof broadCastCombinedEvidence(){
        //reset the timer
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                broadCastCombinedEvidence();
            }
        },60000*requestTime2Wait);
        //check if called by timer, is there request to be handled?
        if(requestNum == 0){
            return null;
        }
        //calculate the root digest of combined evidence by merkle tree
        SecureRandom secureRandom = new SecureRandom();
        String secureSeed = Sha256Hash.of(Integer.toString(secureRandom.nextInt()).getBytes()).toString();
        currentSeedDigest.put(secureSeed,secureSeed);

        Iterator<Map.Entry<String,String>> iterator = currentSeedDigest.entrySet().iterator();
        ArrayList<String> merkleList = new ArrayList<>();
        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            merkleList.add(entry.getValue());
        }
        Collections.sort(merkleList);
        MerkleTrees merkleTrees = new MerkleTrees(merkleList);
        merkleTrees.merkle_tree();
        String digest = merkleTrees.getRoot();
        System.out.print("Root Digest:" + digest);

        //generate the seed digest and serialize it
        String seedDigest = "";
        int size = merkleList.size();
        for (int i = 0;i < size;i++){
            seedDigest += merkleList.get(i);
            seedDigest += "\n";
        }
        //build the tx and broadcast it
        Transaction transaction = new Transaction(Main.params);
        transaction.setLockTime(EvidenceOperatorCode.CombinedEvidence.ordinal());
        transaction.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(Hex.decode(digest)));
        //tx.setLockTime(0);
        try{
            Main.bitcoin.wallet().sendCoins(SendRequest.forTx(transaction));
            Proof proof = new Proof();
            proof.setEviSimpleInfo(digest);
            proof.setSeedDigest(seedDigest);
            proof.setCombinedDigest(digest);
            proofList.getItems().add(proof);
            proof.setTxHash(transaction.getHashAsString());
            proof.setDepth(0);
            proof.setConfirmTimes(0);
            easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
            //load the combined evidence to the database and clean the map and requestNum
            serverDao.createCED(digest,seedDigest,requestNum);
            requestNum = 0;
            currentSeedDigest.clear();
            //add listener to collect tx result
            Main.bitcoin.peerGroup().addBlocksDownloadedEventListener(new BlocksDownloadedEventListener() {
                @Override
                public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                    List<Sha256Hash> hashes = new ArrayList<>();
                    PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
                    tree.getTxnHashAndMerkleRoot(hashes);
                    if (hashes.contains(transaction.getHash())) {
                        Date timeStamp = filteredBlock.getBlockHeader().getTime();
                        proof.setTimestamp(timeStamp);
                        CombinedEvidence e = serverDao.getCombinedEvidence(digest);
                        e.setTxHash(transaction.getHash().toString());
                        e.setTimeStamper(timeStamp);
                        serverDao.modifyCombinedEvidence(e);
                        easyRetrievalMap.remove(proof.getEviSimpleInfo());
                        easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                        //remove the listener
                        Main.bitcoin.peerGroup().removeBlocksDownloadedEventListener(this);
                    }
                }});
            transaction.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                @Override
                public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                    if(confidence.getConfidenceType()== TransactionConfidence.ConfidenceType.DEAD){
                        System.out.println("Double Spend Detect,txhash Verifies!");
                        Transaction doubleSpendTx = confidence.getOverridingTransaction();
                        proof.setTxHash(doubleSpendTx.getHash().toString());
                        proof.setTimestamp(doubleSpendTx.getUpdateTime());
                        proof.setConfirmTimes(1);
                        proof.setDepth(1);
                        CombinedEvidence e = serverDao.getCombinedEvidence(digest);
                        e.setTxHash(doubleSpendTx.getHash().toString());
                        e.setTimeStamper(doubleSpendTx.getUpdateTime());
                        serverDao.modifyCombinedEvidence(e);
                        easyRetrievalMap.remove(proof.getEviSimpleInfo());
                        easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                        doubleSpendTx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                            @Override
                            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                                if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                    return;
                                proof.setDepth(confidence.getDepthInBlocks());
                                proof.setConfirmTimes(confidence.getDepthInBlocks());
                                easyRetrievalMap.remove(proof.getEviSimpleInfo());
                                easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                                if (proof.getDepth() == 3) {
                                    //change the combined evidence state in the database
                                    CombinedEvidence e = serverDao.getCombinedEvidence(digest);
                                    byte b = 1;
                                    e.setIsConfirmed(b);
                                    serverDao.modifyCombinedEvidence(e);
                                    //remove the proof from the observing proofList
                                    proofList.getItems().remove(proof);
                                    //remove the listener
                                    doubleSpendTx.getConfidence().removeEventListener(this);
                                }
                            }
                        });
                        transaction.getConfidence().removeEventListener(this);
                    }else {
                        if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                            return;
                        proof.setDepth(confidence.getDepthInBlocks());
                        proof.setConfirmTimes(confidence.getDepthInBlocks());
                        easyRetrievalMap.remove(proof.getEviSimpleInfo());
                        easyRetrievalMap.put(proof.getEviSimpleInfo(),proof);
                        if (proof.getDepth() == 3) {
                            //change the combined evidence state in the database
                            CombinedEvidence e = serverDao.getCombinedEvidence(digest);
                            byte b = 1;
                            e.setIsConfirmed(b);
                            serverDao.modifyCombinedEvidence(e);
                            //remove the proof from the observing proofList
                            proofList.getItems().remove(proof);
                            //remove the listener
                            transaction.getConfidence().removeEventListener(this);
                        }
                    }
                }
            });

            return proof;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }


    public void onVerifyClicked(ActionEvent actionEvent) {
    }

    public void handleTestBtn(ActionEvent actionEvent) {
        /*
        //code for light weight service test

        String string1 = "ddd";
        String string2 = "eee";
        String string3 = "www";
        String string4 = "qqq";
        String string5 = "ppp";
        String digest = Sha256Hash.of(string1.getBytes()).toString();
        currentSeedDigest.put(digest,digest);
        requestNum++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedRequestNumLabel.setText(String.valueOf(requestNum));
            }
        });
        digest = Sha256Hash.of(string2.getBytes()).toString();
        currentSeedDigest.put(digest,digest);
        requestNum++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedRequestNumLabel.setText(String.valueOf(requestNum));
            }
        });
        receivedRequestNumLabel.setText(String.valueOf(requestNum));
        digest = Sha256Hash.of(string3.getBytes()).toString();
        currentSeedDigest.put(digest,digest);
        requestNum++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedRequestNumLabel.setText(String.valueOf(requestNum));
            }
        });
        digest = Sha256Hash.of(string4.getBytes()).toString();
        currentSeedDigest.put(digest,digest);
        requestNum++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedRequestNumLabel.setText(String.valueOf(requestNum));
            }
        });
        digest = Sha256Hash.of(string5.getBytes()).toString();
        currentSeedDigest.put(digest,digest);
        requestNum++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receivedRequestNumLabel.setText(String.valueOf(requestNum));
            }
        });
        Proof proof = broadCastCombinedEvidence();
        System.out.println(proof.getSeedDigest());
        */

        //test for evidence chain api


        //test step 1:
        /*ECKey testEcKey = new ECKey();
        String email = "goodmusic@163.com";
        System.out.println("priKey:   " + testEcKey.getPrivateKeyAsHex());
        try {
            broadCastEvidence("goodmusic@163.com","60a11806a76ce4fb31fd9f93fc77ac70a3aecbfe4642d19cd104e70df36a7e67",testEcKey,null);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //test step 2:
        /*
        String email = "goodmusic@163.com";
        ECKey testEcKey = ECKey.fromPrivate(Hex.decode("efee05d9c5969ad38b0c9eb0dfabf01b21bba38fb666a1f401aaab9ba7767941"));
        String parentTxHash = "5734caae89633e0562fd52a74410009e862faa7fea011464eaed71a8ddc88ceb";
        try{
            broadCastEvidence(email,"60a11806a76ce4fb31fd9f93fc77ac70a3aecbfe4642d19cd104e70d326a7e67",testEcKey,parentTxHash);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        //EvidenceChain evidenceChain = new EvidenceChain();
        //evidenceChain.setEmail(email);
        //String digestList = evidence1.getDigest + "\n" + finalDigest;
        //evidenceChain.setDigestList(digestList);

        /*
        Sha256Hash txhash = Sha256Hash.wrap("60a11806a76ce4fb31fd9f93fc77ac70a3aecbfe4642d19cd104e70df36a7e67");
        Transaction tx = bitcoin.wallet().getTransaction(txhash);
        //System.out.println(tx);
        Transaction txTest = new Transaction(Main.params);
        ECKey myEckey = ECKey.fromPrivate(Hex.decode("a95cb1b7a8af7c4f25a7cbf42c6a2da87e252b249674ee1d6b380d0bc10fa55f"));
        System.out.println(myEckey);
        String ss = "sdssddsdsdsd";
        txTest.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(ss.getBytes()));
        txTest.addOutput(Coin.valueOf(5000),myEckey.toAddress(Main.params));
        try{
            SendRequest sendRequest = SendRequest.forTx(txTest);
            bitcoin.wallet().completeTx(sendRequest);
            //txTest.getInput(0).clearScriptBytes();
            txTest.addInput(tx.getOutput(2));
            Sha256Hash hashForTxTest = txTest.hashForSignature(1,tx.getOutput(2).getScriptPubKey(), Transaction.SigHash.ALL,false);
            ECKey.ECDSASignature ecdsaSignature = myEckey.sign(hashForTxTest);
            Script inputScript = ScriptBuilder.createInputScript(new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL,false));
            txTest.getInput(1).setScriptSig(inputScript);
            sendRequest = SendRequest.forTx(txTest);
            TransactionInput txIn = txTest.getInput(0);
            List<TransactionSigner> signers = bitcoin.wallet().getTransactionSigners();
            KeyBag maybeDecryptingKeyBag = new DecryptingKeyBag(bitcoin.wallet(), sendRequest.aesKey);
            Script scriptPubKey = txIn.getConnectedOutput().getScriptPubKey();
            RedeemData redeemData = txIn.getConnectedRedeemData(maybeDecryptingKeyBag);
            txIn.setScriptSig(scriptPubKey.createEmptyInputScript(redeemData.keys.get(0), redeemData.redeemScript));
            TransactionSigner.ProposedTransaction proposal = new TransactionSigner.ProposedTransaction(txTest);
            for (TransactionSigner signer : signers) {
                if (!signer.signInputs(proposal, maybeDecryptingKeyBag))
                    System.out.println("warning! Signing Error Existed!");
            }
            new MissingSigResolutionSigner(sendRequest.missingSigsMode).signInputs(proposal, maybeDecryptingKeyBag);
            System.out.println(txTest);
            bitcoin.peerGroup().broadcastTransaction(txTest);
        }catch (NullPointerException e){
            System.out.print("warning");
        }catch (Exception e){
            e.printStackTrace();
        }*/
        /*
        //audit test 1
        Transaction tx = new Transaction(bitcoin.params());
        tx.setLockTime(EvidenceOperatorCode.Authenticity.ordinal());
        ECKey ecKey = new ECKey();
        Sha256Hash evidenceDigest = Sha256Hash.wrap("cb14d762eafa116f25c3809845950b6f9e8c0ae5b367e19893974ea74ef4d989");
        tx.addOutput(Coin.ZERO,ScriptBuilder.createOpReturnScript(ecKey.sign(evidenceDigest).encodeToDER()));
        tx.addOutput(Coin.valueOf(5000),ecKey.toAddress(bitcoin.params()));
        SendRequest sendRequest = SendRequest.forTx(tx);
        try {
            //bitcoin.wallet().completeTx(sendRequest);
            bitcoin.wallet().sendCoins(sendRequest);
            System.out.println(tx);
        }catch (Exception e){
            e.printStackTrace();
        }
         */
        //audit test 2
        /*
        Transaction tx = bitcoin.wallet().getTransaction(Sha256Hash.wrap("cc7aba116b736021ba20d4a068a2e3fff2966b6ecc0a823bdc8308b4b152b910"));
        System.out.print(tx);
        String rawCode = tx.getOutput(1).getScriptPubKey().toString();
        String[] s = StringUtils.splitString(rawCode,"[");
        String[] s1 = StringUtils.splitString(s[1],"]");
        String sig = s1[0];*/
        bitcoin.wallet().isRequiringUpdateAllBloomFilter();


    }

    public void onChangeCombinedEviConfig(MouseEvent mouseEvent) {
        try{
            int changeNum1 = Integer.parseInt(request2WaitField.getText());
            int changeNum2 = Integer.parseInt(Time2WaitField.getText());
            if(changeNum1 <= 0 || changeNum2 <= 0){
                System.out.println("Warning: INVALID SETTING!");
                return;
            }
            requestNum2Wait = changeNum1;
            requestTime2Wait = changeNum2;
            System.out.println("Setting Success: changes will be implemented in the next combined evidence!");
        }catch (NumberFormatException e){
            e.printStackTrace();
            System.out.println("Warning: ONLY NUM CAN BE INPUT!");
            return;
        }
    }

    class WebDatabaseService extends Service<Channel>{
        @Override
        protected Task<Channel> createTask() {
            return new Task<Channel>(){
                @Override
                protected Channel call() throws Exception {
                    EventLoopGroup bossGroup = new NioEventLoopGroup();
                    EventLoopGroup workerGroup = new NioEventLoopGroup();
                    try{
                        ServerBootstrap b = new ServerBootstrap();
                        b.group(bossGroup, workerGroup);
                        b.channel(NioServerSocketChannel.class);
                        b.childHandler(new ChannelInitializer<SocketChannel>(){
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {

                                ChannelPipeline pipeline = ch.pipeline();
                                ch.pipeline().addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                                ch.pipeline().addLast(new ObjectEncoder());
                                pipeline.addLast("handler", new SimpleChannelInboundHandler<Message>(){
                                    private ConcurrentHashMap<String,String> validcodeMap = new ConcurrentHashMap<>();
                                    private final Random RANDOM = new SecureRandom();
                                    private Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d,32,32);
                                    private ConcurrentHashMap<String,String> activeUser = new ConcurrentHashMap<>();
                                    private ConcurrentHashMap<String,String> activeAudit = new ConcurrentHashMap<>();
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, Message msg){
                                        Message message = (Message)msg;
                                        switch (message.getType()){
                                            case AUTHNOTE:
                                                AuthNote authNote = (AuthNote)message;
                                                AuthInstitution authSome = serverDao.getAuth(activeAudit.get(ctx.channel().remoteAddress().toString()));
                                                if(authSome.getAuthPbkeyList().contains(authNote.getEcPbkey())){
                                                    Transaction authNoteTx = new Transaction(bitcoin.params(),authNote.getAuthTx());
                                                    String tr1 = getOpreturnOutputIndex(authNoteTx).getScriptPubKey().toString();
                                                    String[] tr2 = StringUtils.splitString(tr1,"[");
                                                    String[] tr3 = StringUtils.splitString(tr2[1],"]");
                                                    String sig = tr3[0];
                                                    ECKey signer = ECKey.fromPublicOnly(Hex.decode(authNote.getEcPbkey()));
                                                    if(signer.verify(Sha256Hash.wrap(authNote.getAuthDigest()),TransactionSignature.decodeFromDER(Hex.decode(sig)))){
                                                        bitcoin.peerGroup().broadcastTransaction(authNoteTx);
                                                        bitcoin.wallet().commitTx(authNoteTx);
                                                        //sync the database
                                                        AuthReply authReply = new AuthReply();
                                                        authReply.setAuditDigest(authNote.getAuthDigest());
                                                        authReply.setInstitutionBody(authSome.getId());
                                                        authReply.setAuthTxHash(authNoteTx.getHashAsString());
                                                        authReply.setEvidenceDigest(authNote.getFinalDigest());
                                                        serverDao.createARP(authReply);
                                                        if(authSome.getAuthLog()!=null){
                                                            String authLog = authSome.getAuthLog();
                                                            authLog = authLog + authNote.getFinalDigest() +"\t" + authNote.getAuthDigest() + "\n";
                                                            authSome.setAuthLog(authLog);
                                                        }else {
                                                            String authLog = authNote.getFinalDigest() +"\t" + authNote.getAuthDigest() + "\n";
                                                            authSome.setAuthLog(authLog);

                                                        }
                                                        serverDao.modifyAuth(authSome);
                                                        Proof proof = new Proof();
                                                        proof.setEviSimpleInfo("Audit For" + authNote.getFinalDigest());
                                                        proof.setDepth(0);
                                                        proofList.getItems().add(proof);

                                                        //add listener to the Tx
                                                        Main.bitcoin.peerGroup().addBlocksDownloadedEventListener(new BlocksDownloadedEventListener() {
                                                            @Override
                                                            public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                                                                List<Sha256Hash> hashes = new ArrayList<>();
                                                                PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
                                                                tree.getTxnHashAndMerkleRoot(hashes);
                                                                if (hashes.contains(authNoteTx.getHash())) {
                                                                    Date timeStamp = filteredBlock.getBlockHeader().getTime();
                                                                    AuthReply arp = serverDao.getARP(authNote.getFinalDigest());
                                                                    arp.setAuthTimestamp(timeStamp);
                                                                    serverDao.modifyAuthReply(arp);
                                                                    //remove the listener
                                                                    Main.bitcoin.peerGroup().removeBlocksDownloadedEventListener(this);
                                                                }
                                                            }});
                                                        authNoteTx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                                                            @Override
                                                            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                                                                if(confidence.getConfidenceType() == TransactionConfidence.ConfidenceType.DEAD){
                                                                    Transaction doubleSpendTx = confidence.getOverridingTransaction();
                                                                    AuthReply arp = serverDao.getARP(authNote.getFinalDigest());
                                                                    arp.setAuthTxHash(doubleSpendTx.getHash().toString());
                                                                    arp.setAuthTimestamp(doubleSpendTx.getUpdateTime());
                                                                    serverDao.modifyAuthReply(arp);
                                                                    doubleSpendTx.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                                                                        @Override
                                                                        public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                                                                            if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                                                                return;
                                                                            proof.setDepth(confidence.getDepthInBlocks());
                                                                            if (confidence.getDepthInBlocks() >= 3) {
                                                                                proofList.getItems().remove(proof);
                                                                                AuthReply arp = serverDao.getARP(authNote.getFinalDigest());
                                                                                arp.setConfirmed(true);
                                                                                serverDao.modifyAuthReply(arp);
                                                                                AuthNoteReply authNoteReply = new AuthNoteReply();
                                                                                authNoteReply.setState("Confirmed");
                                                                                authNoteReply.setDigest(authNote.getFinalDigest());
                                                                                ctx.writeAndFlush(authNoteReply);
                                                                                doubleSpendTx.getConfidence().removeEventListener(this);
                                                                            }
                                                                        }
                                                                    });
                                                                    authNoteTx.getConfidence().removeEventListener(this);
                                                                }else {
                                                                    if (confidence.getConfidenceType() != TransactionConfidence.ConfidenceType.BUILDING)
                                                                        return;
                                                                    proof.setDepth(confidence.getDepthInBlocks());
                                                                    if (confidence.getDepthInBlocks() >= 3) {
                                                                        proofList.getItems().remove(proof);
                                                                        AuthReply arp = serverDao.getARP(authNote.getFinalDigest());
                                                                        arp.setConfirmed(true);
                                                                        serverDao.modifyAuthReply(arp);
                                                                        authNoteTx.getConfidence().removeEventListener(this);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                        AuthNoteReply authNoteReply = new AuthNoteReply();
                                                        authNoteReply.setState("Watched");
                                                        authNoteReply.setDigest(authNote.getFinalDigest());
                                                        ctx.writeAndFlush(authNoteReply);
                                                    }else {
                                                        AuthNoteReply authNoteReply = new AuthNoteReply();
                                                        authNoteReply.setState("Invalid signer detected!");
                                                        ctx.writeAndFlush(authNoteReply);
                                                    }
                                                }

                                                break;
                                            case AUTHLOG:
                                                //for informal reg use
                                                /*Authlog authlog = (Authlog)message;
                                                AuthInstitution authInstitution = new AuthInstitution();
                                                authInstitution.setId(authlog.getEmail());
                                                String saltAddedPassword = argon2.hash(2,65536,10,authlog.getPassword());
                                                authInstitution.setSaltAddedPassword(saltAddedPassword);
                                                serverDao.createAuth(authInstitution);*/
                                                //for log in use
                                                Authlog authlog = (Authlog)message;
                                                AuthInstitution authInstitution = serverDao.getAuth(authlog.getEmail());
                                                AuthLogReply authLogReply = new AuthLogReply();
                                                if(argon2.verify(authInstitution.getSaltAddedPassword(),authlog.getPassword())){
                                                    authLogReply.setReply("Success!");
                                                    activeAudit.put(ctx.channel().remoteAddress().toString(),authlog.getEmail());
                                                }else {
                                                    authLogReply.setReply("Login fails,wrong Password!");
                                                }
                                                ctx.writeAndFlush(authLogReply);
                                                break;
                                            case AUTHVALID:
                                                if(!activeAudit.containsKey(ctx.channel().remoteAddress().toString())){
                                                    break;
                                                }
                                                String instiName = activeAudit.get(ctx.channel().remoteAddress().toString());
                                                AuthAddressValidation authAddressValidation = (AuthAddressValidation)message;
                                                ArrayList<String> strList = authAddressValidation.getAddressList();
                                                MerkleTrees merkleTrees = new MerkleTrees(strList);
                                                merkleTrees.merkle_tree();
                                                String root = merkleTrees.getRoot();
                                                Transaction txProof = new Transaction(bitcoin.params(),authAddressValidation.getTxProof());
                                                String rawCode = txProof.getOutput(1).getScriptPubKey().toString();
                                                String[] s = StringUtils.splitString(rawCode,"[");
                                                String[] s1 = StringUtils.splitString(s[1],"]");
                                                if(s1[0].equals(root)){
                                                    bitcoin.wallet().commitTx(txProof);
                                                    AuthInstitution authInsti = serverDao.getAuth(instiName);
                                                    String authPbkeyList = "";
                                                    for(String str:strList){
                                                        authPbkeyList = authPbkeyList + str + "\n";
                                                    }
                                                    authInsti.setAuthPbkeyList(authPbkeyList);
                                                    authInsti.setProofTxHash(txProof.getHashAsString());
                                                    serverDao.modifyAuth(authInsti);
                                                    txProof.getConfidence().addEventListener(new TransactionConfidence.Listener() {
                                                        @Override
                                                        public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                                                            if(confidence.getConfidenceType()!= TransactionConfidence.ConfidenceType.BUILDING){
                                                                System.out.println("Warning: Auth Proof Not Building!");
                                                                return;
                                                            }
                                                            if(confidence.getDepthInBlocks() >= 3){
                                                                 AuthInstitution authInsti = serverDao.getAuth(instiName);
                                                                 authInsti.setProofTxConfirmed(true);
                                                                 serverDao.modifyAuth(authInsti);
                                                                 AuthAddressValidRep arep = new AuthAddressValidRep();
                                                                 arep.setState("Confirmed");
                                                                 ctx.writeAndFlush(arep);
                                                                 txProof.getConfidence().removeEventListener(this);
                                                            }
                                                        }
                                                    });
                                                    AuthAddressValidRep arep = new AuthAddressValidRep();
                                                    arep.setState("Watched");
                                                    ctx.writeAndFlush(arep);
                                                }else {
                                                     System.out.println("Root diff Error");
                                                    AuthAddressValidRep arep = new AuthAddressValidRep();
                                                    arep.setState("Failed");
                                                    ctx.writeAndFlush(arep);
                                                }

                                                break;
                                            case LOGIN:
                                                String emailLog = message.getEmail();
                                                if(serverDao.searchCU(emailLog)){
                                                    String password = ((LoginMsg)message).getPassword();
                                                    ClientUser user = serverDao.getClientUserOnly(emailLog);
                                                    if(argon2.verify(user.getSaltAddedPassword(),password)){
                                                        ctx.writeAndFlush(new PassportMsg());
                                                        activeUser.put(ctx.channel().remoteAddress().toString(),emailLog);
                                                    }else {
                                                        ctx.writeAndFlush(new WrongPasswordMsg());
                                                    }
                                                }else {
                                                    ctx.writeAndFlush(new NoneUserMsg());
                                                }
                                                break;
                                            case REGISTER:
                                                String email = message.getEmail();
                                                if(serverDao.searchCU(email)){
                                                    ReLoginMsg relog = new ReLoginMsg();
                                                    ctx.writeAndFlush(relog);
                                                }else {
                                                    if (validcodeMap.containsKey(email)){
                                                        validcodeMap.remove(email);
                                                    }
                                                    Long valilong = RANDOM.nextLong();
                                                    String code = valilong.toString() + email;
                                                    String validateCode = Sha256Hash.wrap(Sha256Hash.hash(code.getBytes())).toString();
                                                    System.out.println(validateCode);
                                                    processRegister(email,validateCode);
                                                    validcodeMap.put(email,validateCode);
                                                    CheckEmailMsg ckMsg = new CheckEmailMsg();
                                                    ctx.writeAndFlush(ckMsg);
                                                }
                                                break;
                                            case VALIDATE:
                                                String valiRet = ((ValidateMsg)message).getValidateCode();
                                                String emailRecall = message.getEmail();
                                                if(validcodeMap.get(emailRecall).equals(valiRet)){
                                                    String passwordR = ((ValidateMsg)message).getPassword();
                                                    String hash = argon2.hash(2,65536,10,passwordR);
                                                    ECKey ecKey = new ECKey();
                                                    Address address = ecKey.toAddress(Main.params);
                                                    bitcoin.wallet().addWatchedAddress(address,ecKey.getCreationTimeSeconds());
                                                    String eckeyENs = org.bitcoinj.core.Utils.HEX.encode(UtilsUsable.encrypt(ecKey.getPrivateKeyAsHex(),((ValidateMsg) message).getPassword()));
                                                    serverDao.createCU(emailRecall,hash,eckeyENs);
                                                    RegSuccessMsg regSuccessMsg = new RegSuccessMsg();
                                                    regSuccessMsg.setEcKey(address.toString());
                                                    ctx.writeAndFlush(regSuccessMsg);
                                                    activeUser.put(ctx.channel().remoteAddress().toString(),emailRecall);
                                                }else {
                                                    WrongValiMsg wv = new WrongValiMsg();
                                                    ctx.writeAndFlush(wv);
                                                }
                                                break;
                                            case UPLOAD:
                                                //search the active Map to find if the address has a user login
                                                if(activeUser.containsKey(ctx.channel().remoteAddress().toString())){
                                                    UploadMsg uploadMsg = (UploadMsg) message;
                                                    //Judge the upload service type
                                                    if(!uploadMsg.isLightWeightService()){
                                                        //distinct service:
                                                        String upEmail = activeUser.get(ctx.channel().remoteAddress().toString());
                                                        //check whether the user eckey is right
                                                        ClientUser upC = serverDao.getClientUserOnly(upEmail);
                                                        byte[] key = Hex.decode(new String(UtilsUsable.decrypt(Utils.HEX.decode(upC.getEckeyEncrypted()),((UploadMsg) message).getPassword())));
                                                        ECKey ecKey = ECKey.fromPrivate(key);
                                                        if(ecKey.toAddress(Main.params).toString().equals(((UploadMsg) message).getECkeyPbKey())){
                                                            //check whether the evidence build on a parent evidence
                                                            if(uploadMsg.getParentDiggest() == null){
                                                                //if not
                                                                //create the evidence
                                                                serverDao.createEd(uploadMsg.getFinalDiggest(),upEmail,uploadMsg.getEvidence());
                                                                //upload the evidence to the block chain
                                                                try{
                                                                    Proof proof = broadCastEvidence(upEmail,uploadMsg.getFinalDiggest(),ecKey,null);
                                                                    //when upload finishes, transport the uploadSuc message
                                                                    UploadSuccessMsg uploadSuccessMsg = new UploadSuccessMsg();
                                                                    uploadSuccessMsg.setProof(proof);
                                                                    ctx.writeAndFlush(uploadSuccessMsg);
                                                                }catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                            }else {
                                                                //if yes
                                                                //check whether the parent digest valid and whether it is already in a chain
                                                                Evidence evidence = serverDao.getEvidence(uploadMsg.getParentDiggest());
                                                                System.out.println(evidence);
                                                                if(evidence != null &&(evidence.getIsConfirmed() == 1 || easyRetrievalMap.get(evidence.getDigest()).getConfirmTimes() >= 1)){
                                                                    String parentTxHash = evidence.getTxHash();
                                                                    String possibleEnd = serverDao.checkDigestInChain(uploadMsg.getFinalDiggest(),evidence.getDigest(),upEmail);
                                                                    String parentDigest = uploadMsg.getParentDiggest();
                                                                    if(possibleEnd != null){
                                                                        if(possibleEnd.equals("error")){
                                                                            System.out.println("Reuse digest error!");
                                                                            break;
                                                                        }
                                                                        Evidence evidenceEnd = serverDao.getEvidence(possibleEnd);
                                                                        parentTxHash = evidenceEnd.getTxHash();
                                                                        parentDigest = possibleEnd;
                                                                   }else {
                                                                        EvidenceChain evidenceChain = new EvidenceChain();
                                                                        evidenceChain.setEmail(upEmail);
                                                                        evidenceChain.setDigestList(uploadMsg.getParentDiggest() + "\n" + uploadMsg.getFinalDiggest());
                                                                        serverDao.createEC(evidenceChain);
                                                                    }
                                                                   try {
                                                                       serverDao.createEd(uploadMsg.getFinalDiggest(),upEmail,uploadMsg.getEvidence());
                                                                       Proof proof = broadCastEvidence(upEmail,uploadMsg.getFinalDiggest(),ecKey,parentTxHash);
                                                                       proof.setParentDigest(parentDigest);
                                                                       UploadSuccessMsg uploadSuccessMsg = new UploadSuccessMsg();
                                                                       uploadSuccessMsg.setProof(proof);
                                                                       ctx.writeAndFlush(uploadSuccessMsg);
                                                                   }catch (Exception e){
                                                                        e.printStackTrace();
                                                                   }

                                                                }else {
                                                                    System.out.println("Bad request for evidenceChain!");
                                                                }


                                                            }

                                                        }else {
                                                            System.out.println("Bad Eckey!!");
                                                        }
                                                    }else {
                                                        //light service:
                                                        requestNum++;
                                                        Platform.runLater(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                receivedRequestNumLabel.setText(String.valueOf(requestNum));
                                                            }
                                                        });

                                                        String dig = uploadMsg.getFinalDiggest();
                                                        if(serverDao.findDigestInCombined(dig) != null){
                                                            System.out.println("Same Digest Error!");
                                                            break;
                                                        }
                                                        currentSeedDigest.put(dig,dig);
                                                        if(requestNum == requestNum2Wait){
                                                            broadCastCombinedEvidence();
                                                        }
                                                        UploadSuccessMsg upsm = new UploadSuccessMsg();
                                                        upsm.setLightService(true);
                                                        ctx.writeAndFlush(upsm);

                                                    }

                                                }else {
                                                    System.out.println("User Info Error!!");
                                                }
                                                break;
                                            case SYNCREQUEST:
                                                String digest = ((SyncRequestMsg)message).getEviFinalDigest();
                                                if(easyRetrievalMap.containsKey(digest)||serverDao.getEvidence(digest)!=null){
                                                    SyncAnswerMsg syncAnswerMsg = new SyncAnswerMsg();
                                                    AuthReply authReply = serverDao.getARP(digest);
                                                    if(authReply!=null){
                                                        AuditAnswer auditAnswer = new AuditAnswer();
                                                        auditAnswer.setConfirmed(authReply.isConfirmed());
                                                        auditAnswer.setAuthTimestamp(authReply.getAuthTimestamp());
                                                        auditAnswer.setAuditTxHash(authReply.getAuthTxHash());
                                                        auditAnswer.setAuthDigest(authReply.getAuditDigest());
                                                        String name = authReply.getInstitutionBody();
                                                        AuthInstitution aI = serverDao.getAuth(name);
                                                        auditAnswer.setInstitutionName(name);
                                                        auditAnswer.setAuthProofHash(aI.getProofTxHash());
                                                        ArrayList<String> arrayList = auditAnswer.getAuthPbKeys();
                                                        String[] strings = aI.getAuthPbkeyList().split("\n");
                                                        for(String ss:strings){
                                                            arrayList.add(ss);
                                                        }
                                                        auditAnswer.setAuthPbKeys(arrayList);
                                                        syncAnswerMsg.setAuditAnswer(auditAnswer);
                                                    }
                                                    if(easyRetrievalMap.containsKey(digest)){
                                                        syncAnswerMsg.setProof(easyRetrievalMap.get(digest));
                                                    }else {
                                                        Evidence evidence = serverDao.getEvidence(digest);
                                                        Proof proof = new Proof();
                                                        proof.setTxHash(evidence.getTxHash());
                                                        proof.setTimestamp(evidence.getTimeStamp());
                                                        proof.setConfirmTimes(evidence.getIsConfirmed()==1?3:1);
                                                        syncAnswerMsg.setProof(proof);
                                                    }
                                                    ctx.writeAndFlush(syncAnswerMsg);
                                                }else {
                                                    //search combined digest for answer
                                                    if(currentSeedDigest.contains(digest)){
                                                        //means the update not complete, needs waiting
                                                        SyncAnswerMsg syncAnswerMsg = new SyncAnswerMsg();
                                                        syncAnswerMsg.setLightService(true);
                                                        ctx.writeAndFlush(syncAnswerMsg);
                                                    }else {
                                                        //finds the bounded combined digest and get its proof
                                                        String combinedDigest = serverDao.findDigestInCombined(digest);
                                                        if(combinedDigest == null){
                                                            System.out.println("Bad Rerquest from "+ctx.channel().remoteAddress());
                                                            break;
                                                        }
                                                        SyncAnswerMsg syncAnswerMsg = new SyncAnswerMsg();
                                                        syncAnswerMsg.setLightService(true);
                                                        syncAnswerMsg.setProofComplete(true);
                                                        syncAnswerMsg.setProof(easyRetrievalMap.get(combinedDigest));
                                                        ctx.writeAndFlush(syncAnswerMsg);
                                                    }

                                                }
                                                break;
                                            case FORREQ:
                                                String forHash = ((ForensicsRequest)message).getEviHash();
                                                ForensicsAns forensicsAns = new ForensicsAns();
                                                if(((ForensicsRequest)message).isLightService()){
                                                    if(currentSeedDigest.contains(forHash)){
                                                        forensicsAns.setExisted(true);
                                                        forensicsAns.setComplete(false);
                                                    }else {
                                                        String resultDig = serverDao.findDigestInCombined(forHash);
                                                        if(resultDig == null){
                                                            forensicsAns.setExisted(false);
                                                        }else {
                                                            forensicsAns.setExisted(true);
                                                            CombinedEvidence coe = serverDao.getCombinedEvidence(resultDig);
                                                            forensicsAns.setTxHash(coe.getTxHash());
                                                            forensicsAns.setConfirmed(coe.getIsConfirmed()==1);
                                                            forensicsAns.setTimestamp(coe.getTimeStamper());
                                                            forensicsAns.setSeedDigest(coe.getSeedDigest());
                                                        }
                                                    }

                                                }else {
                                                    Evidence forEvidence = serverDao.getEvidence(forHash);
                                                    if(forEvidence == null){
                                                        forensicsAns.setExisted(false);
                                                    }else {
                                                        forensicsAns.setExisted(true);
                                                        Transaction tx = bitcoin.wallet().getTransaction(Sha256Hash.wrap(forEvidence.getTxHash()));
                                                        String sfor = getAvailIdex(tx).getScriptPubKey().toString();
                                                        String[] sfor1 = StringUtils.splitString(sfor,"[");
                                                        String[] sfor2 = StringUtils.splitString(sfor1[1],"]");
                                                        String pbkey = sfor2[0];
                                                        forensicsAns.setEckeyBounded(pbkey);
                                                        if (forEvidence.getTxHash() != null){
                                                            forensicsAns.setTxHash(forEvidence.getTxHash());
                                                            forensicsAns.setTimestamp(forEvidence.getTimeStamp());
                                                        }
                                                        forensicsAns.setConfirmed(forEvidence.getIsConfirmed()==1);
                                                    }
                                                }
                                                ctx.writeAndFlush(forensicsAns);
                                                break;
                                            default: break;
                                        }
                                    }
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
                                        super.channelActive(ctx);
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " inactive !");
                                        activeUser.remove(ctx.channel().remoteAddress().toString());
                                    }

                                });
                            }
                        });
                        ChannelFuture f = b.bind(7878).sync();
                        f.channel().closeFuture().sync();
                        return  f.channel();
                    } catch (Exception e){
                        e.printStackTrace();
                        return null;
                    } finally {
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
                }
            };
        }

        private TransactionOutput getOpreturnOutputIndex(Transaction authNoteTx) {
            for(TransactionOutput o:authNoteTx.getOutputs()){
                if(o.getScriptPubKey().isOpReturn()){
                    return o;
                }
            }
            return null;
        }
    }
}
