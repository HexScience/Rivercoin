/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.networking;

import com.riverssen.core.FullBlock;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.networking.messages.BlockMessage;
import com.riverssen.core.networking.messages.GreetMessage;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.Handler;
import com.riverssen.core.utils.Tuple;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Server implements NetworkManager
{
    public static final String  seedNodeUrl = "http://www.rivercoin.net/developers/api/seednodes.php";

    private Set<String>         ipAddresses;
    private Set<Client>         communications;
    private ContextI            context;
    private ServerSocket        socket;

    public Server(ContextI context) throws Exception
    {
        this.ipAddresses    = new LinkedHashSet<>();
        this.communications = new LinkedHashSet<>();
        this.context        = context;
        this.socket         = new ServerSocket(context.getConfig().getPort());
    }

    public void establishConnection() throws Exception
    {
        addSavedIps();
        addSeedIPs();
        createListener();

        if(ipAddresses.size() == 0)
            throw new Exception("no seed ip address found.");

        establishConnections();
    }

    private void createListener()
    {
        context.getExecutorService().execute(()->{
            while(context.isRunning())
            {
                try{
                    SocketConnection connection = new SocketConnection(socket);
                    communications.add(new Client(connection, context));

                    Thread.sleep(12L);
                } catch (Exception e)
                {
                }
            }
        });
    }

    @Override
    public void broadCastNewTransaction(TransactionI transaction)
    {
        for (Communicator communicator : communications) communicator.sendTransaction(transaction);
    }

    @Override
    @Deprecated
    public void requestLongestForkAndDownload()
    {
        Tuple<Long, Communicator>   tuple = new Tuple(-1, null);
        Handler<Integer>            numfired = new Handler<>(0);
        int maxCommunicators = communications.size();

        for (Client communicator : communications) communicator.requestLatestBlockInfo(context, (chainSize)->{

            if(tuple.getI().longValue() < chainSize)
            {
                tuple.setI(chainSize);
                tuple.setJ(communicator);
            }

            numfired.setI(numfired.getI().intValue() + 1);
        });

        long now = System.currentTimeMillis();

        while(numfired.getI() < maxCommunicators && (System.currentTimeMillis() - now < 2_000))
        {
        }
    }

    @Override
    public Set<Client> getCommunicators()
    {
        return communications;
    }

    @Override
    public int amountNodesConnected()
    {
        int amt = 0;

        for(Client communicator : communications)
            if(communicator.isRelay()) amt ++;
        return amt;
    }

    @Override
    public void downloadLongestChain()
    {
        List<Communicator> nodes = new ArrayList<>();

        for(Communicator communicator : communications)
            if(communicator.isNode()) nodes.add(communicator);

        nodes.sort((a, b)->{ if(a.chainSizeAtHandshake() == b.chainSizeAtHandshake()) return 0;
                                else if(a.chainSizeAtHandshake() > b.chainSizeAtHandshake()) return 1;
                                else return -1;
        });

        context.getExecutorService().execute(()->{
            while(nodes.size() > 0)
            {
                Communicator node = nodes.get(nodes.size() - 1);

                long desiredChainSize = node.getType();

                synchronized (node)
                {
                    String lock = ByteUtil.defaultEncoder().encode58((this.getClass().getName() + System.currentTimeMillis()).getBytes());

                    while(!node.lock(lock)) {}

                    node.requestBlock(context.getBlockChain().currentBlock() + 1, context, lock);
                    FullBlock block = null;

                    try
                    {
                        block = node.receiveBlock();

                        node.unlock(lock);
                    } catch (Exception e)
                    {
                        node.unlock(lock);
                    }

                    if (block == null)
                    {
                        nodes.remove(nodes.size() - 1);
                        continue;
                    }

                    if (block.validate(context) != 0)
                    {
                        nodes.remove(nodes.size() - 1);
                        continue;
                    }

                    context.getBlockChain().insertBlock(block);

                    if (context.getBlockChain().currentBlock() >= desiredChainSize) nodes.clear();
                }
            }
        });
    }

    @Override
    public void sendBlock(FullBlock block)
    {
        for(Client communicator : communications)
            communicator.sendMessage(new BlockMessage(block));
    }

    private void establishConnections()
    {
        for(String ipAddress : ipAddresses)
            connectToIp(ipAddress);
    }

    private void connectToIp(String ip)
    {
        try{
            SocketConnection connection = new SocketConnection(ip, context.getConfig().getPort());
            if(connection.isConnected())
            {
                Client client = new Client(connection, context);
                client.sendMessage(new GreetMessage());

                context.getExecutorService().execute(client);
                communications.add(client);
            }
        } catch (Exception e)
        {
        }
    }

    private void addSavedIps()
    {
        ipAddresses.addAll(getList());
    }

    private void addSeedIPs() throws Exception
    {
        Connection connection = Jsoup.connect(seedNodeUrl);
        Document doc = connection.get();
        String text = doc.body().text();

        String ips[] = text.split("\n");

        for(String string : ips)
            ipAddresses.add(string);
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
        }

        return ips;
    }

    public void terminate()
    {
        for(Client peer : communications) try{peer.closeConnection();} catch (Exception e) {}

        String ipAddresses = "";

        this.ipAddresses.addAll(getList());

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
}
