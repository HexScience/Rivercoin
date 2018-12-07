package com.riverssen.nucleus.consensys;

import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class ForeignFork extends ForkI
{
    private IpAddress       peer;

    @Override
    public boolean add(Block block)
    {
        return false;
    }

    public boolean add(DownloadedBlock downloadedBlock)
    {
        if (peer == null)
        {
            peer = downloadedBlock.getSender();
            blockQueue = new SortedLinkedQueue<>();
            blockQueue.add(downloadedBlock.getBlock());
        }
        else if (downloadedBlock.getSender().equals(peer))
        {
            if (blockQueue.contains(downloadedBlock.getBlock()))
            {
                blockQueue.remove(downloadedBlock.getBlock());
                blockQueue.add(downloadedBlock.getBlock());
            }
            else
                blockQueue.add(downloadedBlock.getBlock());
        }

        return peer.equals(downloadedBlock.getSender());
    }

    @Override
    public Queue<Block> get()
    {
        return blockQueue;
    }

    @Override
    public boolean hasSender(IpAddress sender)
    {
        return peer.equals(sender);
    }
}