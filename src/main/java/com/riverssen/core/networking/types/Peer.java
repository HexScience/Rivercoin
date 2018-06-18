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

package com.riverssen.core.networking.types;

import com.riverssen.core.FullBlock;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.Event;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.networking.Communicator;
import com.riverssen.core.networking.NetworkManager;
import com.riverssen.core.networking.SocketConnection;

import java.io.IOException;
import java.util.Set;

public class Peer implements Communicator
{
    private SocketConnection connection;

    public Peer(SocketConnection connection)
    {
        this.connection = connection;
    }

    @Override
    public void closeConnection() throws IOException
    {

    }

    @Override
    public String getIP()
    {
        return null;
    }

    @Override
    public int getType()
    {
        return 0;
    }

    @Override
    public void readInbox(ContextI context)
    {

    }

    @Override
    public void requestBlock(long block, ContextI context)
    {

    }

    @Override
    public void requestBlockHeader(long block, ContextI context)
    {

    }

    @Override
    public void requestListOfCommunicators(NetworkManager network)
    {

    }

    @Override
    public void requestLatestBlockInfo(ContextI context, Event<Long> event)
    {

    }

    @Override
    public void sendHandShake(long version)
    {

    }

    @Override
    public void sendTransaction(TransactionI transaction)
    {

    }

    @Override
    public void sendBlock(FullBlock block)
    {

    }

    @Override
    public void sendBlockHeader(BlockHeader header)
    {

    }

    @Override
    public void sendListOfCommunicators(Set<Communicator> list)
    {

    }

    @Override
    public void sendLatestBlockInfo(long block, long hashCode)
    {

    }

    @Override
    public boolean isNode()
    {
        return false;
    }
}
