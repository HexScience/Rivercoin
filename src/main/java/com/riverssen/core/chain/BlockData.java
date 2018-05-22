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

package com.riverssen.core.chain;

import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Context;
import com.riverssen.core.transactions.TXIList;
import com.riverssen.core.transactions.TransactionInput;
import com.riverssen.utils.Serializer;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.utils.MerkleTree;

import java.io.*;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class BlockData implements Encodeable
{
    public static final int MAX_BLOCK_SIZE = 4_000_000;
    private MerkleTree  merkleTree;
    private long        time;
    private long        dataSize;
    private Context     context;
    private List<TransactionInput> fees;

    public BlockData()
    {
        this.merkleTree = new MerkleTree();
    }

    public BlockData(long blockID, Context context)
    {
        this.context = context;
        try
        {
            File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block["+blockID+"]");
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
        merkleTree      = new MerkleTree();

        try
        {
            merkleTree.deserialize(stream, null);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean mine()
    {
        return dataSize >= MAX_BLOCK_SIZE;
    }

    public MerkleTree getMerkleTree()
    {
        return merkleTree;
    }

    public boolean transactionsValid()
    {
        List<TransactionI> flat = merkleTree.flatten();
        for(TransactionI token : flat)
            if(!token.valid()) return false;
        return true;
    }

    @Override
    public byte[] getBytes()
    {
        byte bytes[] = null;

        try(Serializer serializer = new Serializer())
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
        if(!token.valid()) return;

        merkleTree.add(token);
        dataSize += token.toJSON().getBytes().length;
    }

//    public void FetchUTXOs(PublicAddress address, List<UTXO> tokens)
//    {
//        for(TransactionI token : merkleTree.flatten())
//        {
//            tokens.addAll(token.getTXIDs());
//        }
//    }

    public TXIList collectOutputs(PublicAddress miner)
    {
        return null;
    }
}