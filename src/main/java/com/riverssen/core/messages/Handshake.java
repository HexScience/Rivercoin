/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.messages;

import com.riverssen.core.Logger;
import com.riverssen.core.headers.Message;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Context;
import com.riverssen.utils.Base58;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Handshake implements Message<Handshake.ShakeInfo>
{
    @Override
    public long header()
    {
        return handshake;
    }

    @Override
    public void send(DataOutputStream stream, ShakeInfo information, Context context)
            throws IOException
    {
        stream.writeUTF("handshake");
        stream.writeLong(context.getVersionBytes());
        stream.writeUTF(Base58.encode(context.getConfig().getCurrentDifficulty().toByteArray()));
        stream.writeUTF(context.getMiner().toString());

        stream.flush();
    }

    @Override
    public ShakeInfo receive(DataInputStream input, Context context)
            throws IOException
    {
        String handshake    = input.readUTF();
        short  version      = input.readShort();
        String difficulty   = input.readUTF();
        String address      = input.readUTF();

        if(handshake.equals("handshake"))
        {
            Logger.err("message mismatch");
            return null;
        }

        ShakeInfo info = new ShakeInfo(version, difficulty, new PublicAddress(address));

        if(!Base58.encode(context.getConfig().getCurrentDifficulty().toByteArray()).equals(info.getDifficulty()))
            Logger.err("difficulty mismatch");

        return info;
    }

    @Override
    public void onReceive(DataInputStream input, Context context, Peer connection)
            throws IOException
    {
        ShakeInfo info = receive(input, context);
        if(info == null) return;

        if(!Base58.encode(context.getConfig().getCurrentDifficulty().toByteArray()).equals(info.getDifficulty()))
            Logger.err("difficulty mismatch");
    }

    public class ShakeInfo{
        long            version;
        String          difficulty;
        PublicAddress   address;

        public ShakeInfo(long version, String difficulty, PublicAddress address)
        {
            this.version    = version;
            this.difficulty = difficulty;
            this.address    = address;
        }

        public long getVersion()
        {
            return version;
        }

        public BigInteger getDifficulty()
        {
            return new BigInteger(Base58.decode(difficulty));
        }

        public PublicAddress getAddress()
        {
            return address;
        }
    }
}
