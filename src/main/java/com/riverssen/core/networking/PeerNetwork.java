package com.riverssen.core.networking;

import com.riverssen.core.*;
import com.riverssen.core.headers.Transaction;
import com.riverssen.utils.TimeUtil;
import com.riverssen.utils.Tuple;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PeerNetwork
{
    private List<Peer>      peers;

    private ServerSocket    socket;
    private BlockPool       blockPool;
    private SolutionPool    solutionPool;
    private TransactionPool transactionPool;

    public PeerNetwork()
    {
        this(8);
    }

    public PeerNetwork(int size)
    {
        peers = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void connect(ExecutorService service) throws IOException
    {
        InetAddress address = InetAddress.getByName("localhost");
        Logger.alert("listening on: " + address.getHostAddress() + ":" + Config.getConfig().PORT);

        socket = new ServerSocket(Config.getConfig().PORT, 100, address);

        service.execute(()->{
            while(RVCCore.get().run())
            {
                try
                {
                    Socket socketpeer = socket.accept();

                    Peer peer = new Peer(socketpeer, service);
                    if(peer.performHandshake())
                    peers.add(peer);

                    peer.requestChainInfo();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addPool(BlockPool blockPool)
    {
    }

    public long GetChainSize()
    {
        return -1;
    }

    public void fetchAllBlocks(List<FullBlock> pool, long latestBlock)
    {
    }

    public void SendMined(FullBlock fullBlock)
    {
        Logger.alert(TimeUtil.getPretty("[H:M:S]") + "["+fullBlock.getBlockID()+"]: broadcasting to peers");
    }

    public void addPool(SolutionPool solutionPool)
    {
    }

    public Tuple<String,Long> getForkInfo()
    {
        return new Tuple<String, Long>("", 0L);
    }

    public void BroadCastNewTransaction(Transaction token)
    {
    }

    public void Update()
    {
        for(Peer peer : peers) peer.receive();
    }
}
