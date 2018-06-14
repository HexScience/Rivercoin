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

import com.riverssen.core.FullBlock;
import com.riverssen.core.block.BlockData;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.Message;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.headers.ContextI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NewBlock implements Message<FullBlock>
{
    @Override
    public long header()
    {
        return newblock;
    }

    @Override
    public void send(DataOutputStream out, FullBlock information, ContextI context)
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
    public FullBlock receive(DataInputStream in, ContextI context)
    {
        BlockHeader header = new BlockHeader(in);
        BlockData data = new BlockData(in);
//        return new FullBlock(header, data, new BlockHeader(header.getBlockID() - 1));
        return null;
    }

    @Override
    public void onReceive(DataInputStream in, ContextI context, Peer connection)
    {
//        RivercoinCore.get().getBlockPool().add(receive(in));
    }
}
