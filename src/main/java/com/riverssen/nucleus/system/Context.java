package com.riverssen.nucleus.system;

import com.riverssen.nucleus.consensys.BlockChain;
import com.riverssen.nucleus.event.EventManager;
import com.riverssen.nucleus.ledger.Ledger;
import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.versioncontrol.VersionControl;
import org.iq80.leveldb.DB;

public interface Context
{
    Config getConfig();
    Serializer getSerializer();
    ServerManager getServerManager();
    Ledger getLedger();
    DB getDB();
    EventManager getEventManager();
    VersionControl getVersionControl();
    BlockChain  getBlockChain();

    boolean keepAlive();
}