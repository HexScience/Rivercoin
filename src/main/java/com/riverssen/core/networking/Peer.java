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
import com.riverssen.core.Logger;
import com.riverssen.core.block.BlockData;
import com.riverssen.core.headers.Message;
import com.riverssen.core.messages.*;
import com.riverssen.core.networking.node.MasterNode;
import com.riverssen.core.system.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Peer
{
    private final static List<Message> messages = new ArrayList<>();
    static {
        messages.add(new NewTransaction());
        messages.add(new Handshake());
        messages.add(new NewBlock());
        messages.add(new NewSolution());
        messages.add(new RequestChainSize());
        messages.add(new NewTransaction());
    }
    private Context             context;
    private Socket              socket;
    private DataOutputStream    stream;
    private DataInputStream     input;
    private boolean             connectionClosed;
    private int                 messageHeaderLength = 64;
    private boolean             run;
    private String              name;
    private long                chainSize;
    private boolean             isNode;

    private static final int    msg_block_header = 0;
    private static final int    msg_block_       = 1;
    private static final int    msg_new_token    = 2;
    private static final int    msg_chain_size_r = 3;

    public Peer(Socket socket, Context context) throws IOException
    {
        this.socket         = socket;
        this.stream         = new DataOutputStream(socket.getOutputStream());
        this.input          = new DataInputStream(socket.getInputStream());
        this.run            = true;
        this.name           = socket.getInetAddress().getHostName();
        this.context        = context;
    }

    public void send()
    {
    }

    public synchronized void receive()
    {
        try
        {
            while(input.available() > 0)
            {
                long header = input.readLong();

                for(Message message : messages)
                    if(message.header() == header)
                    {
                        message.onReceive(input, context, this);
                        return;
                    }

                Logger.err("message corrupted, ignored.");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean performHandshake(Context context)
    {
        try {
            new Handshake().send(stream, null, context);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeConnection()
    {
        try
        {
            stream.writeUTF("bye");
            stream.flush();
            socket.close();
            run = false;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void requestChainInfo()
    {
        new RequestChainSize().send(stream, context.getBlockChain().currentBlock(), context);
    }

    /** this method is redundant, it should be removed or kept for pool mining **/
    public void sendStartMineCommand(long l, long divisionOfLabour, BlockData blockData) {}

    /** Request a list of peers from this peer, this solidifies the decentralization of the project **/
    /** meaning in the future, there will be no need for a central server listing ip addresses. **/
    public Set<String> requestPeerList()
    {
        return new HashSet<>();
    }

    public void setChainSize(long chainSize)
    {
        this.chainSize = chainSize;
    }

    public String getAddress()
    {
        return socket.getRemoteSocketAddress().toString();
    }

    public synchronized void sendMissingBlocks(List<FullBlock> blockList)
    {
        new SendChain().send(stream, blockList, context);
    }

    public void fetch(MasterNode masterNode) {
    }
}