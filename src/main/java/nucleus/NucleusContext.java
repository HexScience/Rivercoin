package nucleus;

import nucleus.ledger.LedgerDatabase;
import nucleus.net.ServerManager;
import nucleus.system.Config;
import nucleus.system.Context;
import nucleus.system.Serializer;

public class NucleusContext implements Context
{
    private Config config;
    private Serializer serializer;
    private ServerManager serverManager;
    private LedgerDatabase ledgerDatabase;

    public NucleusContext()
    {
        this.config = new Config();
        this.serializer = new Serializer();
        this.serverManager = new ServerManager();
        this.ledgerDatabase = new LedgerDatabase();
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
    public LedgerDatabase getLedger()
    {
        return ledgerDatabase;
    }
}
