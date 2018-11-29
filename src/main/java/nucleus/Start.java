package nucleus;

import nucleus.crypto.MnemonicPhraseSeeder;
import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.UnknownCommandException;
import nucleus.util.Base58;
import nucleus.util.FileService;
import nucleus.mining.NKMiner;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.util.FileUtils;
import nucleus.versioncontrol.VersionControl;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import tests.Tests;
import tests.WalletGeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class Start
{
    /**
     * @param args Minimum args = 2 (-ep 'path')
     * @throws Throwable
     */
    public static void main(String args[]) throws Throwable
    {
        List<Integer> usableDevices = new ArrayList<>();
        /** Maximum difficulty to mine; (0) to mine any **/
        double        maxDifficulty = Parameters.MAXIMUM_DIFFICULY;

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
         * Initialize the NKMiner.
         */
        NKMiner miner = NKMiner.init();
        /**
         * Initialize the version control class.
         */
        VersionControl.init();

        /**
         * Create a GoogleDB instance.
         */
        DB db = factory.open(entryPoint.newFile("data").newFile("db").file(), new Options());
        Context context = new NucleusContext(entryPoint, db, miner);
    }
}