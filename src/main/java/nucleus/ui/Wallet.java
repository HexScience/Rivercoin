package nucleus.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javafx.event.ActionEvent;
import nucleus.crypto.KeyChain;
import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.ECLibException;
import nucleus.exceptions.FileServiceException;
import nucleus.exceptions.WalletException;
import nucleus.util.ByteUtil;
import nucleus.util.FileService;
import nucleus.versioncontrol.VersionControl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
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

    KeyChain chain;

    @Override
    public void start(Stage stage) throws Exception
    {
        byte bytes[] = new byte[1024];

        String byteString = "";

        SecureRandom secure = new SecureRandom(ByteUtil.encode(1480202125194240L));


        secure.nextBytes(bytes);

        int index = 0;

        for (int j = 0; j < 32; j ++)
        {
            for (int i = 0; i < 12; i ++)
                byteString += "(byte) 0x" + Integer.toHexString(Byte.toUnsignedInt(bytes[index ++])) + ", ";

            byteString += "\n";

            for (int i = 0; i < 12; i ++)
                byteString += "(byte) 0x" + Integer.toHexString(Byte.toUnsignedInt(bytes[index ++])) + ", ";

            byteString += "\n";

            for (int i = 0; i < 8; i ++)
                byteString += "(byte) 0x" + Integer.toHexString(Byte.toUnsignedInt(bytes[index ++])) + ", ";

            byteString += "\n\n\n";
        }


        System.out.println(byteString);

        System.exit(0);

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

    @FXML
    public void onStep3Next(ActionEvent event)
    {
        if (step2_wordlist.getText().replaceAll("\\s+", " ").replaceAll("\\n", " ").equals(step3_wordlist.getText().replaceAll("\\s+", " ").replaceAll("\\n", " ")))
        {
            seeder.setWords(step2_wordlist.getText());

            try
            {
                chain = new KeyChain(seeder.getSeed());

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

                DataOutputStream stream = (DataOutputStream) file.as(DataOutputStream.class);


                chain.write( password, stream);

                stream.flush();
                stream.close();

                tabPane.getSelectionModel().select(4);
            } catch (ECLibException e)
            {
                generate();
                tabPane.getSelectionModel().select(2);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (FileServiceException e)
            {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            } catch (InvalidKeyException e)
            {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e)
            {
                e.printStackTrace();
            } catch (NoSuchPaddingException e)
            {
                e.printStackTrace();
            } catch (BadPaddingException e)
            {
                e.printStackTrace();
            } catch (InvalidKeySpecException e)
            {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e)
            {
                e.printStackTrace();
            } catch (WalletException e)
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