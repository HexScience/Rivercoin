package com.riverssen.nucleus.consensys;

import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.protocols.protobufs.Block;

public class DownloadedBlock implements Comparable<DownloadedBlock>
{
    private Block       block;
    private IpAddress   sender;

    public DownloadedBlock(Block block, IpAddress sender)
    {
        this.block = block;
        this.sender = sender;
    }

    public Block getBlock()
    {
        return block;
    }

    public IpAddress getSender()
    {
        return sender;
    }

    @Override
    public int compareTo(DownloadedBlock o)
    {
        return o.getBlock().getHeader().getHeight() >= block.getHeader().getHeight() ? -1 : 1;
    }
}