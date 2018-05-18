package com.riverssen.core.messages;

import com.riverssen.core.RVCCore;
import com.riverssen.core.Token;
import com.riverssen.core.networking.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NewTransaction implements Message<Token>
{
    @Override
    public long header()
    {
        return 0;
    }

    @Override
    public void send(DataOutputStream out, Token information)
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
    public Token receive(DataInputStream in)
    {
        return Token.read(in);
    }

    @Override
    public void performAction(DataInputStream in)
    {
        RVCCore.get().getTransactionPool().add(receive(in));
    }
}
