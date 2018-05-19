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

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.RVCCore;
import com.riverssen.core.chain.BlockData;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.headers.Message;
import com.riverssen.core.messages.NewTransaction;
import com.riverssen.core.messages.RequestChain;
import com.riverssen.utils.Base58;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Peer
{
    private final static List<Message> messages = new ArrayList<>();
    static {
        messages.add(new NewTransaction());
//        messages.add()
    }
    private Socket              socket;
    private DataOutputStream    stream;
    private DataInputStream     input;
    private boolean             connectionClosed;
    private int                 messageHeaderLength = 64;
    private boolean             run;
    private String              name;

    private static final int    msg_block_header = 0;
    private static final int    msg_block_       = 1;
    private static final int    msg_new_token    = 2;
    private static final int    msg_chain_size_r = 3;

    public Peer(Socket socket) throws IOException
    {
        this.socket = socket;
        this.stream = new DataOutputStream(socket.getOutputStream());
        this.input  = new DataInputStream(socket.getInputStream());
        this.run    = true;
        this.name   = socket.getInetAddress().getHostName();
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
                        message.performAction(input);
                        return;
                    }

                Logger.err("message corrupted, ignored.");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean performHandshake()
    {
        try
        {
            stream.writeUTF("handshake");
            stream.writeShort(RVCCore.versionBytes);
            stream.writeUTF(Base58.encode(Config.getConfig().TARGET_DIFFICULTY.toBigInteger().toByteArray()));
            stream.writeUTF((Config.getConfig().PUBLIC_ADDRESS));

            stream.flush();

            String handshake = input.readUTF();
            short  version   = input.readShort();
            String difficulty= input.readUTF();
            String address   = input.readUTF();

            if(handshake.equals("handshake"))
            {
                Logger.err("message mismatch");
                return false;
            }

            if(!Base58.encode(Config.getConfig().TARGET_DIFFICULTY.toBigInteger().toByteArray()).equals(difficulty))
                Logger.err("difficulty mismatch");

            return true;
        } catch (IOException e)
        {
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
        new RequestChain().send(stream, 0L);
        Long size = new RequestChain().receive(input);


    }

    /** this method is redundant, it should be removed or kept for pool mining **/
    public void sendStartMineCommand(long l, long divisionOfLabour, BlockData blockData) {}

    /** Request a list of peers from this peer, this solidifies the decentralization of the project **/
    /** meaning in the future, there will be no need for a central server listing ip addresses. **/
    public Set<String> requestPeerList()
    {
        return new HashSet<>();
    }
}