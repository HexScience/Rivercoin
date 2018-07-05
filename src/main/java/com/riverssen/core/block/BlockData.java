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

import com.riverssen.core.headers.Exportable;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.headers.Write;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.transactions.TransactionOutput;
import com.riverssen.core.utils.Serializer;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.utils.MerkleTree;
import com.riverssen.core.utils.SmartDataTransferer;
import com.riverssen.riverssen.RiverFlowMap;
import com.riverssen.riverssen.UTXOMap;

import java.io.*;
import java.util.*;

public class BlockData implements Encodeable, Exportable
{
    public static final int MAX_BLOCK_SIZE = 4_000_000;
    @Write private MerkleTree merkleTree;
    @Write private Set<TransactionOutput> outputs;
    @Write private long time;
    private long dataSize;
    private int validation;

    public BlockData()
    {
        this.outputs = new LinkedHashSet<>();
        this.merkleTree = new MerkleTree();
        this.validation = 1;
    }

    public BlockData(DataInputStream stream, ContextI context)
    {
        load(stream, context);
    }

    private void load(DataInputStream stream, ContextI context)
    {
        merkleTree      = new MerkleTree();
        this.validation = 1;
        this.outputs    = new LinkedHashSet<>();

        try
        {
            merkleTree.deserialize(stream, null);
            for(TransactionI transaction : merkleTree.flatten())
                outputs.addAll(transaction.getOutputs(context.getMiner(), context));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean mine(ContextI context)
    {
        return dataSize >= MAX_BLOCK_SIZE || context.getTransactionPool().getLastTransactionWas(1);
    }

    public MerkleTree getMerkleTree()
    {
        return merkleTree;
    }

    public boolean transactionsValid()
    {
        return validation == 1;
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

    public boolean add(TransactionI token, UTXOMap map, ContextI context)
    {
        if (!token.valid(map, context)) {
            validation = -1;
            return false;
        }

        List<TransactionOutput> outputs = token.generateOutputs(context.getMiner(), map, context);

        context.getUtxoManager().addAll(outputs);
        this.outputs.addAll(outputs);

        merkleTree.add(token);
        dataSize += token.toJSON().getBytes().length;

        return true;
    }

    public void addNoValidation(TransactionI token, UTXOMap map, ContextI context) {
        merkleTree.add(token);

        List<TransactionOutput> outputs = token.generateOutputs(context.getMiner(), map, context);

        context.getUtxoManager().addAll(outputs);

        this.outputs.addAll(outputs);
        dataSize += token.toJSON().getBytes().length;
    }

    public void revertOutputs(UTXOMap map, ContextI context)
    {
        for(TransactionI transactionI : merkleTree.flatten())
            transactionI.revertOutputs(context.getMiner(), map, context);
    }

//    @Override
//    public byte[] header()
//    {
//        return new byte[0];
//    }
//
//    @Override
//    public byte[] content()
//    {
//        return new byte[0];
//    }

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

    public Set<TransactionOutput> getOutputs() {
       return outputs;
    }

    public void validate(RiverFlowMap map, ContextI context)
    {
        if(validation == 1)
        {
            MerkleTree old = merkleTree;
            merkleTree = new MerkleTree();

            for(TransactionI transaction : old.flatten())
                add(transaction, map, context);
        }
    }
}