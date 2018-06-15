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

package com.riverssen.core.networking;

import com.riverssen.core.FullBlock;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.networking.messages.Msg;

import java.io.IOException;
import java.util.Set;

public interface Communicator
{
    void closeConnection() throws IOException;
    String getIP();
    int    getType();

    void readInbox();

    void requestTransaction(ContextI context);
    void requestBlock(ContextI context);
    void requestBlockHeader(ContextI context);
    void requestListOfCommunicators(NetworkI network);
    void requestLatestBlockInfo(ContextI context);

    void sendHandShake(int type);
    void sendTransaction(TransactionI transaction);
    void sendBlock(FullBlock block);
    void sendBlockHeader(BlockHeader header);
    void sendListOfCommunicators(Set<Communicator> list);
    void sendLatestBlockInfo(long block);
}
