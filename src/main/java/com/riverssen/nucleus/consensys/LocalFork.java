package com.riverssen.nucleus.consensys;

import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class LocalFork extends ForkI
{
    public LocalFork()
    {
        this.blockQueue = new SortedLinkedQueue<>();
    }

    @Override
    public boolean add(Block block)
    {
        blockQueue.add(block);
        return true;
    }

    @Override
    public boolean add(DownloadedBlock block)
    {
        return false;
    }

    @Override
    public Queue<Block> get()
    {
        return blockQueue;
    }

    @Override
    public boolean hasSender(IpAddress sender)
    {
        return false;
    }
}
