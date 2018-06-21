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

package com.riverssen.core.block;

import com.riverssen.core.chainedmap.RiverFlowMap;
import com.riverssen.core.headers.Exportable;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.headers.Write;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.transactions.TransactionOutput;
import com.riverssen.core.utils.Serializer;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.utils.MerkleTree;
import com.riverssen.core.utils.SmartDataTransferer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.InflaterInputStream;

public class BlockData implements Encodeable, Exportable
{
    public static final int MAX_BLOCK_SIZE = 4_000_000;
    @Write private MerkleTree merkleTree;
    @Write private long time;
    private long dataSize;
    private ContextI context;

    public BlockData(ContextI context)
    {
        this.context = context;
        this.merkleTree = new MerkleTree();
    }

    public BlockData(long blockID, ContextI context)
    {
        this.context = context;
        try
        {
            File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block[" + blockID + "]");
            DataInputStream stream = new DataInputStream(new InflaterInputStream(new FileInputStream(file)));

            stream.skip(BlockHeader.SIZE);

            load(stream);

            stream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public BlockData(DataInputStream stream)
    {
        load(stream);
    }

    private void load(DataInputStream stream)
    {
        merkleTree = new MerkleTree();

        try
        {
            merkleTree.deserialize(stream, null);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean mine(ContextI context)
    {
        return dataSize >= MAX_BLOCK_SIZE || context.getTransactionPool().getLastTransactionWas(context.getConfig().getAverageBlockTime());
    }

    public MerkleTree getMerkleTree()
    {
        return merkleTree;
    }

    public boolean transactionsValid()
    {
        List<TransactionI> flat = merkleTree.flatten();

//        Set<TransactionI> flat = new LinkedHashSet<>();
//        flat.addAll(merkleTree.flatten());

        for (TransactionI token : flat)
            if (!token.valid(context)) return false;
        return true;
    }

    @Override
    public byte[] getBytes()
    {
        byte bytes[] = null;

        try (Serializer serializer = new Serializer())
        {
            getMerkleTree().serialize(serializer.asDataOutputStream());
            serializer.writeLong(time);

            serializer.flush();
            bytes = serializer.getBytes();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bytes;
    }

    public void set(BlockData body)
    {
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public long getTimeStamp()
    {
        return time;
    }

    public void add(TransactionI token)
    {
        if (!token.valid(context)) return;

        context.getUtxoManager().addAll(token.generateOutputs(context.getMiner(), context));

        merkleTree.add(token);
        dataSize += token.toJSON().getBytes().length;
    }

    public void addNoValidation(TransactionI token) {
        merkleTree.add(token);
        dataSize += token.toJSON().getBytes().length;
    }

    public void revertOutputs(ContextI context)
    {
        for(TransactionI transactionI : merkleTree.flatten())
            transactionI.revertOutputs(context.getMiner(), context);
    }

    @Override
    public byte[] header()
    {
        return new byte[0];
    }

    @Override
    public byte[] content()
    {
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt)
    {
    }

    @Override
    public void export(DataOutputStream dost) throws IOException {
        try {
            getMerkleTree().serialize(dost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}