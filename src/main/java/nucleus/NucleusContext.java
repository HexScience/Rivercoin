package nucleus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import nucleus.event.EventManager;
import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.exceptions.FileServiceException;
import nucleus.ledger.Ledger;
import nucleus.consensys.BlockChain;
import nucleus.util.FileService;
import nucleus.mining.NKMiner;
import nucleus.net.ServerManager;
import nucleus.system.Config;
import nucleus.system.Context;
import nucleus.system.Serializer;
import nucleus.versioncontrol.VersionControl;
import org.iq80.leveldb.DB;

import java.io.IOException;

public class NucleusContext implements Context
{
    private Config          config;
    private BlockChain      chain;
    private Serializer      serializer;
    private ServerManager   serverManager;
    private Ledger          ledgerDatabase;
    private DB              db;
    private EventManager    eventManager;
    private NKMiner         miner;
    private boolean         keepAlive;
    private VersionControl  versionControl;

    public NucleusContext(FileService entryPoint, DB db, NKMiner miner) throws IOException, FileServiceException, GeoIp2Exception, EventFamilyDoesNotExistException
    {
        this.keepAlive = true;
        this.config = new Config();
        this.versionControl = new VersionControl();
        this.serializer = new Serializer();
        this.serverManager = new ServerManager(entryPoint);
        this.ledgerDatabase = new Ledger();
        this.db = db;
        this.eventManager = new EventManager();
        this.chain = new BlockChain(this);
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

    @Override
    public EventManager getEventManager()
    {
        return eventManager;
    }

    @Override
    public VersionControl getVersionControl()
    {
        return versionControl;
    }

    @Override
    public BlockChain getBlockChain()
    {
        return chain;
    }

    @Override
    public boolean keepAlive()
    {
        return keepAlive;
    }
}
