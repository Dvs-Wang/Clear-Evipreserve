package sample.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Main;
import sample.Method.UtilsUsable;
import sample.Module.Evidence;
import sample.Module.Person;


/**
 * Created by WangMingming on 2017/3/5.
 */
public class EvidenceIIlustratorController {
    private Stage tableStage;
    private Main main;
    private Evidence evidence = new Evidence();

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField typeTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextArea usageTextArea;
    @FXML
    private TextField nrp1TextField;
    @FXML
    private TextField nrp2TextField;
    @FXML
    private TextField mrp1TextField;
    @FXML
    private TextField mrp2TextField;
    @FXML
    private TextArea extraTextArea;
    @FXML
    private TextField subnTextField;
    @FXML
    private TextField submTextField;

    public void setTableStage(Stage tableStage){
        this.tableStage = tableStage;
    }

    @FXML
    private void initialize(){
        usageTextArea.setPrefColumnCount(16);
        extraTextArea.setPrefColumnCount(16);
        dateTextField.setPromptText("Format:  year.month.day in yyyy.xx.dd");
        typeTextField.setPromptText("Such as \"Compact\",\"Copyright\",etc");
    }

    @FXML
    private void handleApply(){
        evidence.setDigest(main.getHashOfEvidence());
        if(isMandatoryFullyFilled() && checkOptional()){
            evidence.setName(nameTextField.getText());
            evidence.setUsage(usageTextArea.getText());
            evidence.setProduceDate(UtilsUsable.parse(dateTextField.getText()));
            evidence.setType(typeTextField.getText());
            evidence.setExtraExplaination(extraTextArea.getText());
            evidence.setRelatedPerson1(new Person(mrp1TextField.getText(),nrp1TextField.getText()));
            evidence.setRelatedPerson2(new Person(mrp2TextField.getText(),nrp2TextField.getText()));
            evidence.setSubmitter(new Person(submTextField.getText(),subnTextField.getText()));
            main.setFileNum(evidence.toFile());
            main.setPriEvidence(evidence);
            tableStage.close();
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("There are some errors in the table, please check!");
            alert.showAndWait();
        }
    }

    private boolean isMandatoryFullyFilled(){
        int errorcode = 0;
        if (nameTextField.getText() == null || nameTextField.getText().length() == 0) {
            nameTextField.setStyle("-fx-text-fill: #B82525;");
            nameTextField.setText("No valid evidence name!\n");
            errorcode ++;
        }
        if (typeTextField.getText() == null || typeTextField.getText().length() == 0) {
            typeTextField.setStyle("-fx-text-fill: #B82525;");
            typeTextField.setText("No valid evidence type!\n");
            errorcode ++;
        }
        if (dateTextField.getText() == null || dateTextField.getText().length() == 0 || !UtilsUsable.validDate(dateTextField.getText())) {
            dateTextField.setStyle("-fx-text-fill: #B82525;");
            dateTextField.setText("No valid date! The format is year.month.day in yyyy.xx.dd \n");
            errorcode ++;
        }

        if (usageTextArea.getText() == null || usageTextArea.getText().length() == 0) {
            usageTextArea.setStyle("-fx-text-fill: #B82525;");
            usageTextArea.setText("No valid usage!\n");
            errorcode ++;
        }

        if(errorcode == 0){
            return true;
        }else {
            return false;
        }
    }

    private boolean checkOptional(){
        if (nrp1TextField.getText().length() != 0 && nrp1TextField.getText()!=null && !UtilsUsable.checkEmail(mrp1TextField.getText())){
            mrp1TextField.setStyle("-fx-text-fill: #B82525;");
            mrp1TextField.setText("Wrong email format!");
            return false;
        }else if(nrp2TextField.getText().length() != 0 && nrp2TextField.getText()!=null && !UtilsUsable.checkEmail(mrp2TextField.getText())){
            mrp2TextField.setStyle("-fx-text-fill: #B82525;");
            mrp2TextField.setText("Wrong email format!");
            return false;
        }else if(subnTextField.getText().length() != 0 && subnTextField.getText()!=null && !UtilsUsable.checkEmail(submTextField.getText())){
            submTextField.setStyle("-fx-text-fill: #B82525;");
            submTextField.setText("Wrong email format!");
            return false;
        }else{
            return true;
        }

    }
    @FXML
    private void handleCancel(){
        tableStage.close();
    }


    public void setMain(Main main){
        this.main = main;
    }
}
