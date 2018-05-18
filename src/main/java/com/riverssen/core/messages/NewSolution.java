package com.riverssen.core.messages;

import com.riverssen.core.RVCCore;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.networking.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NewSolution implements Message<Solution>
{
    @Override
    public long header()
    {
        return 3;
    }

    @Override
    public void send(DataOutputStream out, Solution information)
    {
        try
        {
            out.writeLong(header());
            out.write(information.getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Solution receive(DataInputStream in)
    {
        try
        {
            return new Solution(in);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return new Solution();
    }

    @Override
    public void performAction(DataInputStream in)
    {
        RVCCore.get().getSolutionPool().add(receive(in));
    }
}