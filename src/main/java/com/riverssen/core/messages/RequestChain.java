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
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.Message;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.headers.ContextI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestChain implements Message<Long>
{
    @Override
    public long header()
    {
        return requestchain;
    }

    @Override
    public void send(DataOutputStream out, Long information, ContextI context)
    {
        try
        {
            out.writeLong(header());
            out.writeLong(context.getBlockChain().currentBlock());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Long receive(DataInputStream in, ContextI context)
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
    public void onReceive(DataInputStream in, ContextI context, Peer connection)
    {
        final long receive = receive(in, context);

        if(receive < 0) return;

        context.getExecutorService().execute(()->{
            List<FullBlock> blockList = Collections.synchronizedList(new ArrayList<>());

            for(long i = receive; i < context.getBlockChain().currentBlock(); i ++)
                blockList.add(BlockHeader.FullBlock(i, context));

            connection.sendMissingBlocks(blockList);
        });
    }
}
