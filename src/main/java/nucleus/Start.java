package nucleus;

import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.ECLibException;
import nucleus.system.Context;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class Start
{
    public static void main(String args[]) throws ECLibException, IOException
    {
        ECLib.init();
        Options options = new Options();
        DB db = factory.open(new File("./db"),options);
        Context context = new NucleusContext(db);

        nucleus.mochaxx.Generator.generateVM();
    }
}