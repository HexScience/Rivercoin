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

package com.riverssen.core.networking.node;

import com.riverssen.core.FullBlock;
import com.riverssen.core.headers.Message;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Config;

import java.net.Socket;
import java.util.*;

public class MasterNode
{
    private List<Node>              peerNodes;
    private List<Peer>              peerClients;
    private Map<Long, FullBlock>    newBlocks;
    private boolean                 running;
    private PublicAddress           nodeAddress;
    private Config                  config;
    private Socket                  socket;
    private List<Message>           outgoingCommunicationStream;
    private List<Message>           incomingCommunicationStream;

    public MasterNode(Config config)
    {
        this.peerNodes      = new ArrayList<>();
        this.peerClients    = new ArrayList<>();
        this.newBlocks      = Collections.synchronizedMap(new LinkedHashMap<>());
        this.running        = true;
        this.config         = config;
        this.nodeAddress    = config.getMinerAddress();
    }

    private void fetchAll()
    {
        for(Node node : peerNodes)
            node.fetch(this);

        for(Peer peer : peerClients)
            peer.fetch(this);

        decipherMessages();
    }

    private void decipherMessages()
    {
    }

    private void sendAll()
    {
        while(outgoingCommunicationStream.size() > 0)
        {
            Message message = outgoingCommunicationStream.get(0);

            for(Node node : peerNodes)
                node.send(message);

            outgoingCommunicationStream.remove(0);
        }
        chooseSolution();
    }

    private void chooseSolution()
    {
        int numSolutions = newBlocks.size();
        if(numSolutions > 2)
        {
            int middleSolution = numSolutions/2+1;
            deleteList(middleSolution);
        } else sendAll();
    }

    private void deleteList(int deleteFrom)
    {
        while(newBlocks.size() > deleteFrom) newBlocks.remove(deleteFrom + 1);
    }

    public void run()
    {
        while(running)
        {
            fetchAll();
            sendAll();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}