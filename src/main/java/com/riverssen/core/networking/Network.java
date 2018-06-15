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

import com.riverssen.core.headers.ContextI;
import com.riverssen.core.networking.messages.GreetingMessage;
import com.riverssen.core.networking.messages.Msg;
import com.riverssen.core.networking.types.Node;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Network implements NetworkI
{
    public static final String  seedNodeUrl = "http://www.rivercoin.net/developers/api/seednodes.php";
    private Set<String>         ipAddresses;
    private Set<Communicator>   communications;
    private ContextI            context;

    public Network(ContextI contextI)
    {
        this.ipAddresses    = new LinkedHashSet<>();
        this.communications = new LinkedHashSet<>();
        this.context        = contextI;
    }

    public void establishConnection() throws Exception
    {
        addSavedIps();
        addSeedIPs();

        if(ipAddresses.size() == 0)
            throw new Exception("no seed ip address found.");

        establishConnections();
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
                connection.getOutputStream().write(new GreetingMessage(SocketConnection.NODE).data());
                connection.getOutputStream().flush();

                int type = connection.getInputStream().readInt();

                if(type == Msg.greeting)
                {
                    type = connection.getInputStream().readInt();

                    switch (type)
                    {
                        case SocketConnection.CLIENT:
                            break;
                        case SocketConnection.MINER:
                            break;
                        case SocketConnection.NODE:
                                communications.add(new Node(connection));
                            break;
                    }
                }
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
            e.printStackTrace();
        }

        return ips;
    }

    public void terminate()
    {
        for(Communicator peer : communications) try{peer.closeConnection();} catch (Exception e) {}

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
