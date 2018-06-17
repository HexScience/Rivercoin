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

import java.io.IOException;
import java.util.Set;

public interface Communicator
{
    public static final int OP_GREET    = 0;
    public static final int OP_HALT     = 1;
    public static final int OP_TXN      = 2;
    public static final int OP_BLK      = 3;
    public static final int OP_BKH      = 4;
    public static final int OP_PRS      = 5;
    public static final int OP_GREET1   = 6;
    public static final int OP_FAILED   = 7;
    public static final int OP_SUCCESS  = 8;

    public static final int OP_MSG      = 2;

    void closeConnection() throws IOException;
    String getIP();
    int    getType();

    void readInbox(ContextI context);

    void requestBlock(long block, ContextI context);
    void requestBlockHeader(long block, ContextI context);
    void requestListOfCommunicators(NetworkManager network);
    void requestLatestBlockInfo(ContextI context);

    void sendHandShake(long version);
    void sendTransaction(TransactionI transaction);
    void sendBlock(FullBlock block);
    void sendBlockHeader(BlockHeader header);
    void sendListOfCommunicators(Set<Communicator> list);
    void sendLatestBlockInfo(long block);
}
