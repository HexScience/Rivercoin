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

package com.riverssen.core.old_networking.node;

import com.riverssen.core.FullBlock;
import com.riverssen.core.headers.Message;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.old_networking.NodeOutputCommunicator;

import java.net.Socket;
import java.util.Collection;

public class PeerClient implements NodeOutputCommunicator
{
    private Socket socket;

    public PeerClient(Socket socket) {
        this.socket = socket;
    }

    public boolean performHandshake()
    {
        return false;
    }

    public void fetch(MasterNode masterNode)
    {
    }

    public void send(Message message) {
    }

    @Override
    public void sendTransactionPool(Collection<TransactionI> transactions) {

    }

    @Override
    public void sendBlockPool(Collection<FullBlock> blocks) {

    }

    @Override
    public void sendTransaction(TransactionI transactionI) {

    }

    @Override
    public void sendBlock(FullBlock block) {

    }

    @Override
    public void send() {
    }
}