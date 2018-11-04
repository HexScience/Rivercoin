package nucleus.system;

import nucleus.ledger.LedgerDatabase;
import nucleus.net.ServerManager;

public interface Context
{
    Config getConfig();
    Serializer getSerializer();
    ServerManager getServerManager();
    LedgerDatabase getLedger();
}