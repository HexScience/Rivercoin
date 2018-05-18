package com.riverssen.core.messages;

import com.riverssen.core.RVCCore;
import com.riverssen.core.headers.Transaction;
import com.riverssen.core.networking.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NewTransaction implements Message<Transaction>
{
    @Override
    public long header()
    {
        return 0;
    }

    @Override
    public void send(DataOutputStream out, Transaction information)
    {
        try
        {
            out.writeLong(header());
            information.write(out);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Transaction receive(DataInputStream in)
    {
        return Transaction.read(in);
    }

    @Override
    public void performAction(DataInputStream in)
    {
        RVCCore.get().getTransactionPool().add(receive(in));
    }
}
