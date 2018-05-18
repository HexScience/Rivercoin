package com.riverssen.core.messages;

import com.riverssen.core.headers.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestChain implements Message<Long>
{
    @Override
    public long header()
    {
        return 4;
    }

    @Override
    public void send(DataOutputStream out, Long information)
    {
        try
        {
            out.writeLong(header());
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

        return new Long(0);
    }

    @Override
    public void performAction(DataInputStream in)
    {
    }
}
