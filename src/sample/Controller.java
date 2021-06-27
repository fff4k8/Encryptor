package sample;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class Controller {

    private Stage myStage;
    public void setStage(Stage stage) {
        myStage = stage;
    }

    public Label enMessage;
    public Label deMessage;

    public TextField enPathField;
    public TextField dePathField;

    public List<File> enFiles;
    public List<File> deFiles;

    public Button enOpenButton;
    public Button deOpenButton;

    public PasswordField enPassFld;
    public PasswordField dePassFld;

    public Button enCryptButton;
    public Button deCryptButton;

    final FileChooser enFileChooser = new FileChooser();
    final FileChooser deFileChooser = new FileChooser();

    FileChooser.ExtensionFilter desFilter =
            new FileChooser.ExtensionFilter("Encrypted files (*.z1)", "*.z1");

    @FXML
    private void initialize() {
        deFileChooser.getExtensionFilters().add(desFilter);
        setButtons();
    }

    public void setButtons() {

        enOpenButton.setOnAction(event -> {
            List<File> fileList = enFileChooser.showOpenMultipleDialog(myStage);
            if (fileList != null) {
                enFiles = fileList;
                StringBuilder sb = new StringBuilder();
                enFiles.forEach(x -> sb.append(x.getAbsolutePath()).append("; "));
                enPathField.setText(sb.toString());
            }
        });

        enCryptButton.setOnAction(event -> {
            enMessage.setText("");
            if (enPassFld.getText().length() >= 8) {
                for (File file : enFiles) {
                    String path = file.getAbsolutePath();
                    if (path.length() > 4) {
                        try {
                            Encryptor.encrypt(path, enPassFld.getText());
                        } catch (Throwable ex) {
                            System.err.println("error in encrypt tryblock");
                            ex.printStackTrace();
                        }
                    }
                }
                enMessage.setTextFill(Color.STEELBLUE);
                enMessage.setText("Successfully encrypted");
            } else {
                enMessage.setTextFill(Color.RED);
                enMessage.setText("Incorrect password or file path!");
            }
        });

        deOpenButton.setOnAction(event -> {
            List<File> fileList = deFileChooser.showOpenMultipleDialog(myStage);
            if (fileList != null) {
                deFiles = fileList;
                StringBuilder sb = new StringBuilder();
                deFiles.forEach(x -> sb.append(x.getAbsolutePath()).append("; "));
                dePathField.setText(sb.toString());
            }
        });

        deCryptButton.setOnAction(event -> {
            deMessage.setText("");
            if (dePassFld.getText().length() >= 8) {
                for (File file : deFiles) {
                    String path = file.getAbsolutePath();
                    if (path.length() > 4) {
                        try {
                            Decryptor.decrypt(path, dePassFld.getText());
                            deMessage.setTextFill(Color.STEELBLUE);
                            deMessage.setText("Successfully decrypted");
                        } catch (Throwable ex) {
                            System.err.println("error in decrypt try block");
                            ex.printStackTrace();
                            deMessage.setTextFill(Color.RED);
                            deMessage.setText("Incorrect password");
                        }
                    }
                }
            } else {
                deMessage.setTextFill(Color.RED);
                deMessage.setText("Password must be 8 symbols long");
            }
        });
    }
}