package nucleus.system;

import nucleus.ledger.LedgerDatabase;
import nucleus.net.ServerManager;
import org.iq80.leveldb.DB;

public interface Context
{
    Config getConfig();
    Serializer getSerializer();
    ServerManager getServerManager();
    LedgerDatabase getLedger();
    DB getDB();
}