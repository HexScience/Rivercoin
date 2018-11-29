package nucleus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.exceptions.FileServiceException;
import nucleus.ledger.Ledger;
import nucleus.util.FileService;
import nucleus.mining.NKMiner;
import nucleus.net.ServerManager;
import nucleus.system.Config;
import nucleus.system.Context;
import nucleus.system.Serializer;
import org.iq80.leveldb.DB;

import java.io.IOException;

public class NucleusContext implements Context
{
    private Config          config;
    private Serializer      serializer;
    private ServerManager   serverManager;
    private Ledger          ledgerDatabase;
    private DB              db;
    private NKMiner         miner;

    public NucleusContext(FileService entryPoint, DB db, NKMiner miner) throws IOException, FileServiceException, GeoIp2Exception
    {
        this.config = new Config();
        this.serializer = new Serializer();
        this.serverManager = new ServerManager(entryPoint);
        this.ledgerDatabase = new Ledger();
        this.db = db;
        this.miner = miner;
    }

    @Override
    public Config getConfig()
    {
        return config;
    }

    @Override
    public Serializer getSerializer()
    {
        return serializer;
    }

    @Override
    public ServerManager getServerManager()
    {
        return serverManager;
    }

    @Override
    public Ledger getLedger()
    {
        return ledgerDatabase;
    }

    @Override
    public DB getDB()
    {
        return db;
    }
}
