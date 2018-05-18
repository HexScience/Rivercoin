package com.riverssen.core.messages;

import com.riverssen.core.networking.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChainSize implements Message<Long>
{
    @Override
    public long header()
    {
        return 2;
    }

    @Override
    public void send(DataOutputStream out, Long information)
    {
        try
        {
            out.writeLong(header());
            out.writeLong(information);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Long receive(DataInputStream in)
    {
        try
        {
            return in.readLong();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return new Long(-1);
    }

    @Override
    public void performAction(DataInputStream in)
    {
    }
}
