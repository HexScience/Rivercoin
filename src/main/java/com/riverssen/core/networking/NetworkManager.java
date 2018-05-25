package com.riverssen.core.networking;

import com.riverssen.core.*;
import com.riverssen.core.events.TerminateEvent;
import com.riverssen.core.headers.Event;
import com.riverssen.core.headers.Listener;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.system.Context;
import com.riverssen.utils.TimeUtil;
import com.riverssen.utils.Truple;
import com.riverssen.utils.Tuple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class NetworkManager implements Listener
{
    private List<Peer>                              peers;

    private ServerSocket                            socket;
    private BlockPool                               blockPool;
    private TransactionPool                         transactionPool;
    private Set<String>                             ipAddresses;
    private final Context                           context;
    private Map<Peer, Truple<Peer, String, Long>>   forkInfo;

    public NetworkManager(Context context)
    {
        this.context    = context;
        peers           = Collections.synchronizedList(new ArrayList<>());
        this.forkInfo   = new HashMap<>();
    }

    public synchronized void connect(ExecutorService service) throws Exception
    {
        this.ipAddresses = new HashSet<>();

        /** get the seed ip address from a central server **/
        try{
            String json = Jsoup.connect(context.getConfig().getUniquePeerLink()).ignoreContentType(true).execute().body();

            JSONObject object = new JSONObject(json);

            JSONArray array = object.getJSONArray("ip");

            int start = 1;

            for(int i = start; i < array.length(); i ++)
                ipAddresses.add(array.getString(i));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        /** if central server is unavailable, try to connect to any ip addresses stored on disk **/

        if(ipAddresses.size() == 0)
            ipAddresses.addAll(getList());


        /** if no addresses were found then throw an exception with a useful message. **/
        if(ipAddresses.size() == 0)
            throw new ConnectException("cannot find any peers: try to hardcode some peer addresses into the network.info file.");

        listenToIncomingConnections(service);
        connectToPeers(service);
    }

    /** listen for incoming connections **/
    /** this method is unsafe, with java's garbage collection, unused sockets/peers can fill up memory **/
    private void listenToIncomingConnections(ExecutorService service) throws Exception
    {
        InetAddress address = InetAddress.getByName("localhost");
        socket = new ServerSocket(context.getConfig().getPort(), 100, address);

        Logger.alert("listening on: " + address.getHostAddress() + ":" + context.getConfig().getPort());

        service.execute(()->{
            while(context.isRunning())
            {
                try
                {
                    socket.setSoTimeout(Integer.MAX_VALUE);
                    Socket socketpeer = socket.accept();

                    Peer peer = new Peer(socketpeer, context);
                        /** no need to perform handshake, the peer client will the send the request **/
                        Set<String> list = peer.requestPeerList();
                        ipAddresses.addAll(list);
                        peer.requestChainInfo();
                        peers.add(peer);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /** send 15 connections to possible node addresses and connect **/
    private void connectToPeers(ExecutorService service)
    {
        service.execute(()->{
            while(peers.size() < 15 && ipAddresses.size() > 0)
            {
                try{
                    /** horrible get(0) function, since HashSet doesn't let us get by index **/
                    String address = null;
                    for(String ip : ipAddresses) if(address == null) address = ip; else break;
                    if(address.equals("null"))
                    {
                        ipAddresses.remove(address);
                        continue;
                    }

                    Socket socket = new Socket(address, context.getConfig().getPort());

                    Peer peer = new Peer(socket, context);
                    if(peer.performHandshake(context))
                    {
                        Set<String> list = peer.requestPeerList();
                        ipAddresses.addAll(list);
                        peer.requestChainInfo();
                        peers.add(peer);
                    }
                    else socket.close();
                    ipAddresses.remove(address);
                } catch (Exception e) {}
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

    public Tuple<String,Long> getForkInfo()
    {
        return new Tuple<String, Long>("", 0L);
    }

    public void BroadCastNewTransaction(TransactionI token)
    {
    }

    public void Update()
    {
        for(Peer peer : peers) peer.receive();
    }

    public Set<String> getList()
    {
        Set<String> ips = new HashSet<>();

        try {
            File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "network.info");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = "";

            while((line = reader.readLine()) != null) ips.add(line);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return ips;
    }

    public void terminate()
    {
        for(Peer peer : peers) peer.closeConnection();

        String ipAddresses = "";

        for(String peer : this.ipAddresses) ipAddresses += peer + "\n";

        try {
            File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "network.info");

            FileWriter writer = new FileWriter(file);

            writer.write(ipAddresses);

            writer.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof TerminateEvent) terminate();
    }

    public List<Peer> getConnections() {
        return peers;
    }

    public void addForeignAddresses(List<String> addresses)
    {
        ipAddresses.addAll(addresses);
    }
}
