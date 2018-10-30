package com.riverssen.net;

import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.block.FullBlock;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.transactions.Transaction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Peer implements Runnable
{
    private ContextI context;
    private Node   parent;
    private long   chainSize;
    private Socket peerSocket;
    private String address;
    private long pung;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Peer(Node node, Socket peer) throws IOException {
        inputStream = new DataInputStream(peer.getInputStream());
        outputStream = new DataOutputStream(peer.getOutputStream());
        pung = System.currentTimeMillis();
        address = peer.getInetAddress().getHostAddress();
        this.parent = node;
    }

    public void ping() throws IOException {
        Message message = new Message(MessageType.PING);
        message.add().writeLong(chainSize);
        message.send(outputStream);
    }

    public void pong() throws IOException {
        Message message = new Message(MessageType.PONG);
        message.add().writeLong(chainSize);
        message.send(outputStream);
    }

    public void requestBlock(long blockID) throws IOException
    {
        Message message = new Message(MessageType.GETBLOCK);
        message.add().writeLong(blockID);
        message.send(outputStream);
    }

    public void sendBlock(FullBlock block) throws IOException
    {
        Message message = new Message(MessageType.GETBLOCK);
        block.export(message.add());
        message.send(outputStream);
    }

    public void requestBlockHeader(long blockID) throws IOException
    {
        Message message = new Message(MessageType.GETBLOCKHEADER);
        message.add().writeLong(blockID);
        message.send(outputStream);
    }

    public void sendBlockHeader(BlockHeader block) throws IOException
    {
        Message message = new Message(MessageType.GETBLOCK);
        block.export(message.add());
        message.send(outputStream);
    }

    public void requestChainSize() throws IOException
    {
        Message message = new Message(MessageType.GETCHAINSIZE);
        message.send(outputStream);
    }

    public void sendChainSize(long size) throws IOException
    {
        Message message = new Message(MessageType.CHAINSIZE);
        message.add().writeLong(size);
        message.send(outputStream);
    }

    public void requestChain(long startingBlock) throws IOException
    {
        Message message = new Message(MessageType.GETCHAIN);
        message.add().writeLong(startingBlock);
        message.send(outputStream);
    }

    public void sendChain(FullBlock[] blocks) throws IOException
    {
        for (FullBlock block : blocks)
            sendBlock(block);
    }

    public void requestChainHeaders(long startingBlock) throws IOException
    {
        Message message = new Message(MessageType.GETCHAINSIZE);
        message.add().writeLong(startingBlock);
        message.send(outputStream);
    }

    public void sendChainHeaders(BlockHeader[] blocks) throws IOException
    {
        for (BlockHeader block : blocks)
            sendBlockHeader(block);
    }

    public void broadcastTransaction(TransactionI transaction) throws IOException
    {
        Message message = new Message(MessageType.TRANSACTION);
        transaction.export(message.add());
        message.send(outputStream);
    }

    private static TransactionI messageToTransaction(Message message)
    {
        return null;
    }

    private static FullBlock messageToBlock(Message message)
    {
        return null;
    }

    @Override
    public void run() {
        while (parent.keepAlive() && isPung())
        {
            try {
                Message message = receive();
                if (message.getType().equals(MessageType.PONG))
                    chainSize = message.consume().toPongMessage().chainSize;

                if (message != null)
                {
                    pung = System.currentTimeMillis();

                    if (!message.isConsumed())
                    {
                        switch (message.getType())
                        {
                            case TRANSACTION:
                                context.getTransactionPool().addRelayed(messageToTransaction(message));
                                break;
                            case BLOCK:
                                context.getBlockChain().que(address, messageToBlock(message));
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Message receive() throws IOException {
        if (inputStream.available() > 0)
            return new Message().receive(inputStream);

        return null;
    }

    /** return true if there was any activity in the past 5 minutes **/
    private boolean isPung()
    {
        return System.currentTimeMillis() - pung <= (300_000L);
    }

    private void stop() throws IOException {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        peerSocket.close();
    }

    private String getAddress()
    {
        return address;
    }
}