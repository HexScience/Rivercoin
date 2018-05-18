package com.riverssen.core.messages;

import com.riverssen.core.FullBlock;
import com.riverssen.core.RVCCore;
import com.riverssen.core.chain.BlockData;
import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.headers.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NewBlock implements Message<FullBlock>
{
    @Override
    public long header()
    {
        return 1;
    }

    @Override
    public void send(DataOutputStream out, FullBlock information)
    {
        try
        {
            out.writeLong(header());
            out.write(information.getHeader().getBytes());
            out.write(information.getBody().getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public FullBlock receive(DataInputStream in)
    {
        BlockHeader header = new BlockHeader(in);
        BlockData data = new BlockData(in);
        return new FullBlock(header, data, new BlockHeader(header.getBlockID() - 1));
    }

    @Override
    public void performAction(DataInputStream in)
    {
        RVCCore.get().getBlockPool().add(receive(in));
    }
}
