package nucleus.system;

import nucleus.ledger.Ledger;
import nucleus.net.ServerManager;
import org.iq80.leveldb.DB;

public interface Context
{
    Config getConfig();
    Serializer getSerializer();
    ServerManager getServerManager();
    Ledger getLedger();
    DB getDB();
}