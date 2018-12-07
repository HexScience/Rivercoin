package com.riverssen.nucleus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.riverssen.nucleus.event.EventManager;
import com.riverssen.nucleus.exceptions.EventFamilyDoesNotExistException;
import com.riverssen.nucleus.exceptions.FileServiceException;
import com.riverssen.nucleus.ledger.Ledger;
import com.riverssen.nucleus.consensys.BlockChain;
import com.riverssen.nucleus.util.FileService;
import com.riverssen.nucleus.net.ServerManager;
import com.riverssen.nucleus.system.Config;
import com.riverssen.nucleus.system.Context;
import com.riverssen.nucleus.system.Serializer;
import com.riverssen.nucleus.versioncontrol.VersionControl;
import org.iq80.leveldb.DB;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NucleusContext implements Context
{
    private Config          config;
    private BlockChain      chain;
    private Serializer      serializer;
    private ServerManager   serverManager;
    private Ledger          ledgerDatabase;
    private DB              db;
    private EventManager    eventManager;
    private AtomicBoolean   keepAlive;
    private VersionControl  versionControl;

    public NucleusContext(FileService entryPoint, DB db) throws IOException, FileServiceException, GeoIp2Exception, EventFamilyDoesNotExistException
    {
        this(entryPoint, db, false);
    }

    public NucleusContext(FileService entryPoint, DB db, boolean runOnDifferentThread) throws IOException, FileServiceException, GeoIp2Exception, EventFamilyDoesNotExistException
    {
        this.keepAlive = new AtomicBoolean(true);
        this.config = new Config();
        this.versionControl = new VersionControl();
        this.db = db;
        this.serializer = new Serializer(this, entryPoint.newFile("cdb"));

        this.eventManager = new EventManager(this);
        this.ledgerDatabase = new Ledger();
        this.serverManager = new ServerManager(this, entryPoint);
        this.serverManager.launch();
        this.chain = new BlockChain(this);
        if (runOnDifferentThread)
        {
            Thread thread = new Thread(chain);
            thread.setDaemon(true);
            thread.start();
        }
        else
            this.chain.run();
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
        return keepAlive.get();
    }
}
