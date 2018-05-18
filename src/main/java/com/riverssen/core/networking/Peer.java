package com.riverssen.core.networking;

import com.riverssen.core.Config;
import com.riverssen.core.Logger;
import com.riverssen.core.RVCCore;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.messages.ChainSize;
import com.riverssen.core.messages.NewTransaction;
import com.riverssen.core.messages.RequestChain;
import com.riverssen.utils.Base58;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

    public Peer(Socket socket, ExecutorService service) throws IOException
    {
        this.socket = socket;
        this.stream = new DataOutputStream(socket.getOutputStream());
        this.input  = new DataInputStream(socket.getInputStream());
        this.run    = true;
        this.name   = socket.getInetAddress().getHostName();
        service.execute(()->
        {
            while(RVCCore.get().run() && run)
            {
                try
                {
                    int version     = input.readInt();//headerBuffer.getShort();
                    int typeOfMSG   = input.readShort();//headerBuffer.getShort();
                    int sizeOfMSG   = input.readShort();//headerBuffer.getInt();

                    byte message[] = new byte[sizeOfMSG];

                    switch (typeOfMSG)
                    {
                        case msg_block_:
                            break;
                        case msg_new_token:
                            MessageWrapper.newTokenMessage(message, version);
                            break;
                        case msg_chain_size_r:
                            stream.write(MessageWrapper.wrapChainSize());
                        case msg_block_header:break;
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    connectionClosed = true;
                }
            }
        });
    }

    public synchronized void sendSolution(Solution solution)
    {
        try
        {
            stream.writeUTF(MessageWrapper.solutionMessage(solution));
        } catch (IOException e)
        {
            e.printStackTrace();
            connectionClosed = true;
        }
    }

    public Solution receiveSolution()
    {
        return null;
    }

    public void send()
    {
    }

    private void newTransaction()
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
}