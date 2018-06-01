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

package com.riverssen.core.networking.node;

import com.riverssen.core.FullBlock;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.networking.NodeOutputCommunicator;
import com.riverssen.utils.EncodeableString;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class PeerNode implements NodeOutputCommunicator
{
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream  inputStream;

    public PeerNode(Socket socket) {
        this.socket             = socket;
        try {
            this.outputStream       = new DataOutputStream(socket.getOutputStream());
            this.inputStream        = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void performHandshake() {
        String msg = socket.toString() + ":" + System.currentTimeMillis();
        String coded = new EncodeableString(msg).encode58();

        try {
            outputStream.write(TCP);
            outputStream.writeInt(HELLO);
            outputStream.writeUTF(coded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTransactionPool(Collection<TransactionI> transactions)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream      bdops  = new DataOutputStream(stream);

        try{
            bdops.writeByte(TCP);
            bdops.writeInt(TRANSACTION_LIST);
            bdops.writeInt(transactions.size());
            for(TransactionI transactionI : transactions)
                transactionI.export(bdops);

            bdops.flush();
            bdops.close();

            this.outputStream.write(stream.toByteArray());
            this.outputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBlockPool(Collection<FullBlock> blocks) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream      bdops  = new DataOutputStream(stream);

        try{
            bdops.writeByte(TCP);
            bdops.writeInt(BLOCK_LIST);
            bdops.writeInt(blocks.size());
            for(FullBlock block : blocks)
                block.export(bdops);

            bdops.flush();
            bdops.close();

            this.outputStream.write(stream.toByteArray());
            this.outputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTransaction(TransactionI transactionI) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream      bdops  = new DataOutputStream(stream);

        try{
            bdops.writeByte(TCP);
            bdops.writeInt(TRANSACTION);
            transactionI.export(bdops);

            bdops.flush();
            bdops.close();

            this.outputStream.write(stream.toByteArray());
            this.outputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBlock(FullBlock block) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream      bdops  = new DataOutputStream(stream);

        try{
            bdops.writeByte(TCP);
            bdops.writeInt(BLOCK);
            block.export(bdops);

            bdops.flush();
            bdops.close();

            this.outputStream.write(stream.toByteArray());
            this.outputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Attempt To Send New Messages And Failed Messages **/
    @Override
    public void send()
    {
    }
}