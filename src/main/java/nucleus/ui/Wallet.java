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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javafx.event.ActionEvent;
import nucleus.NucleusContext;
import nucleus.Start;
import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.*;
import nucleus.mining.NKMiner;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.TransactionInput;
import nucleus.protocols.transaction.TransactionOutput;
import nucleus.protocols.transactionapi.TransactionPayload;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.Base58;
import nucleus.util.FileService;
import nucleus.util.FileUtils;
import nucleus.versioncontrol.VersionControl;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class Wallet extends Application implements Initializable
{
    static String args[];

    public static void main(String... args)
    {
        Wallet.args = args;
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

    @FXML
    Text receiving_address_txt;

    @FXML
    Text receive_pubkey_txt;

    @FXML
    Text privatekey_txt;

    @FXML
    ListView<String> recipient_list;

    @FXML
    TextField recipient_field;

    @FXML TextField amount_field;

    @FXML TextField feepmb_field;

    nucleus.crypto.Wallet wallet;

    TransactionInput inputs[];

    Context context;

    void start(String ... args) throws Throwable
    {
        List<Integer> usableDevices = new ArrayList<>();
        /** Maximum difficulty to mine; (0) to mine any **/
        double        maxDifficulty = nucleus.system.Parameters.MAXIMUM_DIFFICULY;

        FileService entryPoint = null;

        String COPYRIGHT_txt = FileUtils.readUTF(Start.class.getResourceAsStream("/GeoLiteC/COPYRIGHT.txt"));
        byte[] GeoLiteC_mmdb = FileUtils.readBytesRAW(Start.class.getResourceAsStream("/GeoLiteC/GeoLiteC.mmdb"));
        String LICENSE_txt = FileUtils.readUTF(Start.class.getResourceAsStream("/GeoLiteC/LICENSE.txt"));
        String README_txt = FileUtils.readUTF(Start.class.getResourceAsStream("/GeoLiteC/README.txt"));


        Queue<String> cmdQueue = new PriorityQueue<>();

        for (String arg : args)
            cmdQueue.add(arg);

        for (int i = 0; i < cmdQueue.size(); i ++)
        {
            String cmd = cmdQueue.poll();

            switch (cmd)
            {
                case "-ep":
                case "-entrypoint":
                    File file = new File(cmdQueue.poll() + File.separator + "NuC");
                    if (file.mkdirs())
                    {
                        entryPoint = new FileService(file);
                        entryPoint.newFile("GeoLiteC").file().mkdirs();
                        entryPoint.newFile("data").file().mkdirs();
                        FileUtils.writeUTF(entryPoint.newFile("data").newFile("ipdb.dfx").file(), FileUtils.readUTF(Start.class.getResourceAsStream("/data/ipdb.dfx")));
                        FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("COPYRIGHT.txt").file(), COPYRIGHT_txt);
                        FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("LICENSE.txt").file(), LICENSE_txt);
                        FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("README.txt").file(), README_txt);
                        FileUtils.writeBytesRAW(entryPoint.newFile("GeoLiteC").newFile("GeoLiteC.mmdb").file(), GeoLiteC_mmdb);
                    }
                    else if (file.exists())
                        entryPoint = new FileService(file);
                    else throw new Throwable("specified entry point does not exist.");
                    break;

                case "-d":
                case "-devices":
                    while (cmdQueue.peek() != null && !cmdQueue.peek().equals(";"))
                        usableDevices.add(Integer.parseInt(cmdQueue.poll()));
                    break;

                case "-md":
                case "-max_difficulty":
                    maxDifficulty = Double.parseDouble(cmdQueue.poll());
                    break;
                default:
                    throw new UnknownCommandException("unknown command '" + cmd + "' please check the wiki for usage information.");
            }
        }

        if (entryPoint == null)
            throw new Throwable("No entry point set, please refer to the wiki for more information.");

        if (!entryPoint.newFile("data").file().exists())
        {
            entryPoint.newFile("data").file().mkdirs();
            FileUtils.writeUTF(entryPoint.newFile("data").newFile("ipdb.dfx").file(), FileUtils.readUTF(Start.class.getResourceAsStream("/data/ipdb.dfx")));
        }

        if (!entryPoint.newFile("GeoLiteC").file().exists())
        {
            entryPoint.newFile("GeoLiteC").file().mkdirs();
            FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("COPYRIGHT.txt").file(), COPYRIGHT_txt);
            FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("LICENSE.txt").file(), LICENSE_txt);
            FileUtils.writeUTF(entryPoint.newFile("GeoLiteC").newFile("README.txt").file(), README_txt);
            FileUtils.writeBytesRAW(entryPoint.newFile("GeoLiteC").newFile("GeoLiteC.mmdb").file(), GeoLiteC_mmdb);
        }

        if (!entryPoint.newFile("chain").file().exists())
            entryPoint.newFile("chain").file().mkdirs();

        /**
         * Initialize the EC Util Library.
         */
        ECLib.init();
        /**
         * Initialize the version control class.
         */
        VersionControl.init();

        /**
         * Create a GoogleDB instance.
         */
        DB db = factory.open(entryPoint.newFile("data").newFile("db").file(), new Options());
        context = new NucleusContext(entryPoint, db, null);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        try{
            start(args);
        } catch (Throwable throwable)
        {
            throw new Exception(throwable.getMessage());
        }

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
            String data = wallet.getAddress().toString();

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
            wallet = new nucleus.crypto.Wallet(fileService, password);
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

        receiving_address_txt.setText("Your Address Is:\n" + wallet.getAddress().toString());
        receive_pubkey_txt.setText("Your Public Key Is:\n" + wallet.getBase58EncodedPublicKey(true));

        inputs = context.getLedger().getBalanceTable(wallet.getAddress()).getAllOutputs(null);

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

                wallet = new nucleus.crypto.Wallet(file, password, seeder.getSeed());

                seeder = null;

                gotoLoggedInScreen();
            } catch (Throwable e)
            {
            }
        }
    }

    @FXML
    public void onSendButtonPressed(ActionEvent event)
    {
        tabPane.getSelectionModel().select(5);
    }

    @FXML
    public void onReceiveButtonPressed(ActionEvent event)
    {
        tabPane.getSelectionModel().select(6);
    }

    @FXML
    public void onSendBackButtonPressed(ActionEvent event)
    {
        tabPane.getSelectionModel().select(4);
    }

    @FXML
    public void onReceiveBackButtonPressed(ActionEvent event)
    {
        tabPane.getSelectionModel().select(4);
    }

    @FXML
    public void onCopyAddressButtonPressed(ActionEvent event)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(wallet.getAddress().toString()), null);
    }

    @FXML
    public void onCopyPubKeyButtonPressed(ActionEvent event)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(wallet.getBase58EncodedPublicKey(false)), null);
    }

    @FXML
    public void onCopyPrivateKeyButtonPressed(ActionEvent event) throws Throwable
    {
        String password = "default";

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Password");
        dialog.setContentText("Please type in your password.");

        String string = dialog.showAndWait().get();

        if (string.length() > 0)
            password = string;

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(nucleus.crypto.Wallet.WIFPrivateKey(wallet.getPrivateKey(password))), null);
    }

    @FXML
    public void onAddRecipientButtonPressed(ActionEvent event) throws Throwable
    {
        String recipient = recipient_field.getText();
        String amount = amount_field.getText();

        BigDecimal decimal = new BigDecimal(amount).multiply(new BigDecimal(nucleus.system.Parameters.SATOSHIS_PERCOIN));

        if (decimal.longValue() < nucleus.system.Parameters.MINIMUM_TRANSACTION)
            return;

        amount = new BigDecimal(decimal.longValue()).divide(new BigDecimal(nucleus.system.Parameters.SATOSHIS_PERCOIN), 11, BigDecimal.ROUND_UP).toPlainString();

        if (!ECLib.ValidECAddress(recipient))
            return;

        for (int i = 0; i < recipient_list.getItems().size(); i++)
        {
            String _recipient_ = recipient_list.getItems().get(i);

            if (_recipient_.split(" :: ")[0].equals(recipient))
            {
                recipient_list.getItems().set(i, recipient + " :: " + new BigDecimal(amount).add(new BigDecimal(_recipient_.split(" :: ")[1])));
                return;
            }
        }

        recipient_list.getItems().add(recipient + " :: " + amount + " :: --" + decimal.longValue() + " satoshis---");
    }

    @FXML
    public void onSendTransactionButtonPressed(ActionEvent event) throws Throwable
    {
        if (feepmb_field.getText().isEmpty())
            feepmb_field.setText(nucleus.system.Parameters.satoshisToCoin(nucleus.system.Parameters.PRICE_PER_NETWORK_MBYTE).toPlainString() + "");

        long totalBalance = context.getLedger().getBalanceTable(wallet.getAddress()).collectiveBalance(context);

        long total = 0;
        long fee = nucleus.system.Parameters.coinToSatoshis(feepmb_field.getText());

        if (inputs == null)
            return;

        TransactionOutput outputs[] = new TransactionOutput[recipient_list.getItems().size() + 2];

        for (int i = 0; i < recipient_list.getItems().size(); i ++)
        {
            String recipient = recipient_list.getItems().get(i);

            String splitup[] = recipient.split(" :: ");

            String address = splitup[0];
            String amount  = splitup[1];

            outputs[i] = new TransactionOutput();
            long longval = 0;
            outputs[i].setValue(longval = new BigDecimal(amount).multiply(BigDecimal.valueOf(nucleus.system.Parameters.SATOSHIS_PERCOIN)).longValue());
            outputs[i].setSpendScript(TransactionPayload.P2PKH_lock(new Address(Base58.decode(address))));

            total += longval;
        }

        long totalBytes = 0;

        for (TransactionInput input : inputs)
            totalBytes += input.size();

        fee = totalBytes * fee;//new BigDecimal(totalBytes).divide(new BigDecimal(1000000), 34, BigDecimal.ROUND_UP).multiply(new BigDecimal(nucleus.system.Parameters.PRICE_PER_NETWORK_MBYTE)).max(new BigDecimal(nucleus.system.Parameters.PRICE_PER_NETWORK_MBYTE))

        outputs[outputs.length - 2] = new TransactionOutput();
        outputs[outputs.length - 2].setValue(fee);
        outputs[outputs.length - 2].setSpendScript(new byte[] {TransactionPayload.Op.APPEND_COINBASE.opcode});


        outputs[outputs.length - 1] = new TransactionOutput();
        outputs[outputs.length - 1].setValue(Math.max(0, (totalBalance - total) - fee));
        outputs[outputs.length - 1].setSpendScript(null);

        TransactionOutput feeOutput = outputs[outputs.length - 2];
        TransactionOutput returnOutput = outputs[outputs.length - 1];

        long totalFee = outputs.length / fee;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This transaction has a total cost of \"" + nucleus.system.Parameters.satoshisToCoin(total + totalFee).toPlainString() + "\" coins, continue?\n" +
                "Breakdown:\n\tTotal: " + nucleus.system.Parameters.satoshisToCoin(total).toPlainString() + "\n\tFees: " + nucleus.system.Parameters.satoshisToCoin(totalFee).toPlainString(), ButtonType.YES, ButtonType.NO);

        if (alert.showAndWait().get().equals(ButtonType.NO))
            return;

        String password = "default";

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Password");
        dialog.setContentText("Please type in your password.");

        String string = dialog.showAndWait().get();

        if (string.length() > 0)
            password = string;
    }

    @FXML
    public void onClearRecipientListButtonPressed(ActionEvent event) throws Throwable
    {
        recipient_list.getItems().clear();
    }

    @FXML
    public void onRemoveRecipientButtonPressed(ActionEvent event) throws Throwable
    {
        if (recipient_list.getSelectionModel().getSelectedIndices().size() > 0)
            for (int i : recipient_list.getSelectionModel().getSelectedIndices())
                recipient_list.getItems().remove(i);
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