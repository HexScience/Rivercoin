package nucleus.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javafx.event.ActionEvent;
import nucleus.crypto.KeyChain;
import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.*;
import nucleus.util.FileService;
import nucleus.versioncontrol.VersionControl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Wallet extends Application implements Initializable
{
    public static void main(String... args)
    {
        /**
         * Initialize the EC Util Library.
         */
        ECLib.init();
        /**
         * Initialize the NKMiner.
         */
        VersionControl.init();

        launch();
    }

    @FXML
    TabPane tabPane;
    @FXML
    TextArea step2_wordlist;
    @FXML
    TextArea step3_wordlist;

    Stage stage;

    MnemonicPhraseSeeder seeder;
    String wordList = "";

    @FXML
    PasswordField password_field;

    @FXML
    ImageView qr_image_preview;

    Wallet wallet;

    @Override
    public void start(Stage stage) throws Exception
    {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/wallet_v2.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("Nucleus Wallet");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/LSD_icon.png")));

        stage.setResizable(false);

        if (System.getProperty("os.name").toLowerCase().contains("mac"))
            com.apple.eawt.Application.getApplication().setDockIconImage(ImageIO.read(getClass().getResourceAsStream("/LSD_icon.png")));

        stage.show();
    }

    private BufferedImage getQRImage()
    {
        try
        {
            String data = chain.pair().getAddress().toString();

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 372, 345);

            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param event
     *
     * Intro screen Create New Wallet Button
     */
    @FXML
    public void onCreateNewWallet(ActionEvent event)
    {
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    public void onOpenExistingWallet(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open An Existing Wallet!");
        File file = fileChooser.showOpenDialog(stage);

        if (file == null)
            return;

        FileService fileService = new FileService(file);

        String password = "default";

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Password");
        dialog.setContentText("Please type in your password.");

        String string = dialog.showAndWait().get();

        if (string.length() > 0)
            password = string;

        try
        {
            chain = new KeyChain(fileService, password);
//            chain.read(password, (DataInputStream) fileService.as(DataInputStream.class));

            gotoLoggedInScreen();
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param event
     * Wallet Creator Step 1 Back Button
     */
    @FXML
    public void onStep1Back(ActionEvent event)
    {
        tabPane.getSelectionModel().select(0);
    }

    private void generate()
    {
        seeder = new MnemonicPhraseSeeder();

        wordList = seeder.getString().toString();
        String words[] = wordList.split("\\s+");

        String readable = "";

        for (int i = 0; i < words.length; i ++)
            if (i % 5 == 0 && i > 0)
                readable += words[i] + " \n";
            else
                readable += words[i] + " ";

        step2_wordlist.setText(readable);
    }

    @FXML
    public void onCreateNewWalletBTN(ActionEvent event)
    {
        generate();

        tabPane.getSelectionModel().select(2);
    }

    @FXML
    public void onStep2Back(ActionEvent event)
    {
        tabPane.getSelectionModel().select(1);
    }

    @FXML
    public void onStep3Back(ActionEvent event)
    {
        tabPane.getSelectionModel().select(2);
    }

    @FXML
    public void onStep2Next(ActionEvent event)
    {
        tabPane.getSelectionModel().select(3);
    }

    public void gotoLoggedInScreen()
    {
        BufferedImage qrImage = getQRImage();

        if (qrImage != null)
            qr_image_preview.setImage(SwingFXUtils.toFXImage(qrImage, null));

        tabPane.getSelectionModel().select(4);
    }

    @FXML
    public void onStep3Next(ActionEvent event)
    {
        /**
         * Check if the user input matches with the provided seed words
         */
        if (step2_wordlist.getText().replaceAll("\\s+", " ").replaceAll("\\n", " ").equals(step3_wordlist.getText().replaceAll("\\s+", " ").replaceAll("\\n", " ")))
        {
            seeder.setWords(step2_wordlist.getText());

            try
            {
                String password = password_field.getText();

                if (password.length() == 0)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "You did not choose an encryption password; this is unsafe. Proceed?", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Warning");

                    Optional<ButtonType> choice = alert.showAndWait();
                    if (choice.get().equals(ButtonType.YES))
                        password = "default";
                    else throw new WalletException("");
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Your Wallet!");
                File file_ = fileChooser.showSaveDialog(stage);

                if (file_ == null)
                    throw new IOException("No file chosen.");

                FileService file = new FileService(file_.toString() + ".nwf");

                chain = new KeyChain(file, password, seeder.getSeed());

                seeder = null;

                gotoLoggedInScreen();
            } catch (Throwable e)
            {
            }
        }
    }

    @FXML
    public void onExit(ActionEvent event)
    {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }
}