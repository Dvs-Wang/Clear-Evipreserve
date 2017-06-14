package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.bitcoinj.core.Sha256Hash;
import sample.Controller.ShowEvidenceDetailController;
import sample.Module.Evidence;
import sample.Controller.EvidenceIIlustratorController;
import sample.Controller.NoticeController;
import sample.Controller.RootInterfaceController;


import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootInterface;
    private Sha256Hash hashOfEvidence;
    private int fileNum;
    private Evidence priEvidence;
    public RootInterfaceController r;
    public ShowEvidenceDetailController showEvidenceDetailController;


    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Evidence Preservation App Demo");
        initRootInterface();

    }

    /**
     * Initializes the root interface.
     */
    public void initRootInterface() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getClassLoader().getResource("View/RootInterface.fxml"));
            rootInterface = (AnchorPane) loader.load();

            r = loader.getController();
            r.setMain(this);

            // Show the scene containing the root interface.
            Scene scene = new Scene(rootInterface);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    r.save();
                    if(r.getOverWriteMsg().get() != null){
                        r.getEventLoopGroup().shutdownGracefully();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNoticePage(){
        FXMLLoader noticePageLoader = new FXMLLoader();
        noticePageLoader.setLocation(Main.class.getClassLoader().getResource("View/Notice.fxml"));
        try{
            Pane noticePane = (Pane) noticePageLoader.load();
            Stage noticeStage = new Stage();
            noticeStage.setTitle("Notice");
            noticeStage.initModality(Modality.WINDOW_MODAL);
            noticeStage.initOwner(primaryStage);
            Scene noticeScene = new Scene(noticePane);
            noticeStage.setScene(noticeScene);

            NoticeController noco = noticePageLoader.getController();
            noco.setNoticeStage(noticeStage);
            noco.setMain(this);
            noticeStage.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showEvidenceIllustratorPage(){
        FXMLLoader evidenceIllustratorLoader = new FXMLLoader();
        evidenceIllustratorLoader.setLocation(Main.class.getClassLoader().getResource("View/EvidenceIIlustrator.fxml"));
        try {
            AnchorPane evidenceIllustratorPane = (AnchorPane)evidenceIllustratorLoader.load();
            Stage evidenceIllustratorStage = new Stage();
            evidenceIllustratorStage.setTitle("EvidenceIllustrator");
            evidenceIllustratorStage.initModality(Modality.WINDOW_MODAL);
            evidenceIllustratorStage.initOwner(primaryStage);
            Scene evidenceIllustratorScene = new Scene(evidenceIllustratorPane);
            evidenceIllustratorStage.setScene(evidenceIllustratorScene);

            EvidenceIIlustratorController eController = evidenceIllustratorLoader.getController();
            eController.setMain(this);
            eController.setTableStage(evidenceIllustratorStage);
            evidenceIllustratorStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void showEvidenceDetailPage(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getClassLoader().getResource("View/ShowEvidenceDetail.fxml"));
        try{
            Pane showEvidenceDetailPane = (Pane)fxmlLoader.load();
            Stage showEvidenceStage = new Stage();
            showEvidenceStage.setTitle("Evidence Details:");
            showEvidenceStage.initModality(Modality.WINDOW_MODAL);
            showEvidenceStage.initOwner(primaryStage);
            Scene showEvidenceDetailScene = new Scene(showEvidenceDetailPane);
            showEvidenceStage.setScene(showEvidenceDetailScene);

            showEvidenceDetailController = fxmlLoader.getController();
            showEvidenceDetailController.setMain(this);
            showEvidenceDetailController.setEvidence(priEvidence);
            showEvidenceDetailController.setPriUser(r.getUserOnline());
            showEvidenceDetailController.setStage(showEvidenceStage);
            showEvidenceStage.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void setHashOfEvidence(Sha256Hash evidence){
        this.hashOfEvidence = evidence;
    }

    public Sha256Hash getHashOfEvidence(){
        return this.hashOfEvidence;
    }

    public void setFileNum(int fileNum){
        this.fileNum = fileNum;
    }

    public  int getFileNum(){
        return this.fileNum;
    }

    public Evidence getPriEvidence(){ return priEvidence;}

    public void setPriEvidence(Evidence priEvidence){
        this.priEvidence = priEvidence;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
