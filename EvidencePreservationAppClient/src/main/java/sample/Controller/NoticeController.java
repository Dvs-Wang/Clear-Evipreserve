package sample.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import sample.Main;

/**
 * Created by WangMingming on 2017/3/3.
 */
public class NoticeController {


    @FXML
    private Button OkBt;
    private Stage noticeStage;
    private Main main;
    public TextArea noticeTextArea;

    @FXML
    public void handleOk(){
        OkBt.setDisable(true);
        main.showEvidenceIllustratorPage();
        noticeStage.close();
    }

    @FXML
    private void initialize(){
        noticeTextArea.setPrefRowCount(20);
        noticeTextArea.setPrefColumnCount(20);

    }

    public void setNoticeStage(Stage noticeStage){
        this.noticeStage = noticeStage;
    }
    public void setMain(Main main){ this.main = main; }
}
