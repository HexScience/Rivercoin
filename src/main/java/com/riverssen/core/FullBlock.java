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

package com.riverssen.core;

import com.riverssen.core.block.BlockData;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.*;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.transactions.RewardTransaction;
import com.riverssen.core.utils.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;

public class FullBlock implements Encodeable, JSONFormattable, Exportable
{
    public static int err_not_valid = 1;
    public static int err_mrkl = 2;
    public static int err_transactions = 3;
    public static int err_timestamp = 4;

    /** parent header file **/
    private BlockHeader parent;
    /** this blocks hash **/
    private String      hash;
    /** this blocks header **/
    private BlockHeader header;
    /** this blocks body **/
    private BlockData   body;

    private long        timeCreated;

    public FullBlock(BlockHeader header, BlockData data, BlockHeader parent)
    {
        this.header = header;
        this.body = data;
        this.parent = parent;
        this.timeCreated = System.currentTimeMillis();
    }

    public FullBlock(long lastBlock, BlockHeader parent)
    {
        this.header = new BlockHeader();
        this.body   = new BlockData();
        this.parent = parent;
        this.header.setBlockID(lastBlock + 1);
        this.timeCreated = System.currentTimeMillis();
    }

    public FullBlock(DataInputStream in)
    {
    }

    public synchronized String getHashAsString()
    {
        return header.getHashAsString();
    }

    public synchronized long getBlockID()
    {
        return header.getBlockID();
    }

    public synchronized int validate(ContextI context)
    {
        return validate(getBlockID() > 0 ? new BlockHeader(getBlockID() - 1, context) : null, context);
    }

    public synchronized int validate(BlockHeader parent, ContextI context)
    {
        byte pHash[] = null;

        if (parent == null)
            pHash = new byte[32];
        else pHash = parent.getHash();

        if(parent == null && getBlockID() > 0) return err_not_valid;

        /** validate merkle root **/
        if (!body.getMerkleTree().hash().equals(header.getMerkleRootAsString())) return err_mrkl;

        /** validate transactions **/
        if (!body.transactionsValid()) return err_transactions;

        /** verify timestamp **/
        if (body.getTimeStamp() != header.getTimeStampAsLong()) return err_timestamp;

        /** verify block started mining at least lastblock_time + blocktime/5 after **/
        if (body.getTimeStamp() <= (header.getTimeStampAsLong() + (context.getConfig().getAverageBlockTime() / 5))) return err_timestamp;

        /** verify nonce **/
        HashAlgorithm algorithm = context.getHashAlgorithm(pHash);
        ByteBuffer data = getBodyAsByteBuffer();
        data.putLong(data.capacity() - 8, header.getNonceAsInt());
        String hash = algorithm.encode16(data.array());

        if(!this.hash.equals(hash)) return 5;
        if(new BigInteger(hash, 16).compareTo(header.getDifficultyAsInt()) > 0) return 6;

        return 0;
    }

    public synchronized void add(TransactionI token)
    {
        body.add(token);
    }

    /**
     * mine the block
     **/
    public synchronized void mine(ContextI context)
    {
        byte parentHash[] = this.parent != null ? this.parent.getHash() : new byte[32];

        HashAlgorithm algorithm     = context.getHashAlgorithm(parentHash);
        BigInteger    difficulty    = context.getDifficulty();
        PublicAddress miner         = context.getMiner();

        String difficultyHash = HashUtil.hashToStringBase16(difficulty.toByteArray());
        while (difficultyHash.length() < 64) difficultyHash = "0" + difficultyHash;

        Logger.alert("--------------------------------");
        Logger.alert("[" + TimeUtil.getPretty("H:M:S") + "][" + header.getBlockID() + "]: new job {"+ algorithm.getClass().getSimpleName() + ":" + (difficultyHash) + "}");

        /** add the reward BEFORE mining the block **/
        body.add(new RewardTransaction(miner, this));

        //System.out.println(new RewardTransaction(miner).toJSON());

        this.body.setTime(System.currentTimeMillis());
        ByteBuffer data = getBodyAsByteBuffer();

        long nonce = 0;
        this.hash = algorithm.encode16(data.array());

        while (new BigInteger(hash, 16).compareTo(difficulty) > 0) { data.putLong(data.capacity() - 8, ++nonce); this.hash = algorithm.encode16(data.array()); }

        body.getMerkleTree().buildTree();
        header.setHash(algorithm.encode(data.array()));
        header.setParentHash(parent.getHash());
        header.setMerkleRoot(body.getMerkleTree().encode(algorithm));
        header.setTimeStamp(System.currentTimeMillis());
        header.setDifficulty(difficulty);
        header.setNonce(nonce);
        header.setMinerAddress(miner.toString().getBytes());
        this.hash = header.getHashAsString();

        double time = (System.currentTimeMillis() - this.body.getTimeStamp()) / 1000.0;
        Logger.alert("[" + TimeUtil.getPretty("H:M:S") + "][" + header.getBlockID() + "]: hashing took '" + time + "s' '" + this.hash + "'");

        /** send solution **/
        context.getBlockPool().Send(this);

        /** check solutions **/
        List<FullBlock> solutions = context.getBlockPool().Fetch();

        for (FullBlock block : solutions)
        {
            if (block.getBlockID() < getBlockID()) continue;
            if(!block.getHeader().getParentHash().equals(getHeader().getParentHash())) continue;
            if (block.validate(parent, context) == 0)
            {
                if (!(block.getHeader().getTimeStampAsLong() > getHeader().getTimeStampAsLong() - (150000 + new Random(System.currentTimeMillis()).nextInt(150000))
                        && block.getHeader().getTimeStampAsLong() < getHeader().getTimeStampAsLong())) continue;

                this.header.set(block.getHeader());
                this.body.set(block.getBody());
                this.hash = block.getHashAsString();

                Logger.err("[" + TimeUtil.getPretty("H:M:S") + "][" + header.getBlockID() + "]: share orphaned to '" + this.hash + "'");
            }
        }
    }

    public synchronized BlockHeader getHeader()
    {
        return header;
    }

    @Override
    public byte[] getBytes()
    {
        return ByteUtil.concatenate(parent.getHash(), body.getBytes());
    }

    public synchronized BlockData getBody()
    {
        return body;
    }

    private synchronized ByteBuffer getBodyAsByteBuffer()
    {
        byte bodydata[] = getBytes();
        ByteBuffer data = ByteBuffer.allocate(bodydata.length + 8);
        data.put(bodydata);
        data.putLong(0);
        data.flip();
        return data;
    }

    public synchronized void serialize(ContextI context)
    {
        File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block["+getBlockID()+"]");

        try
        {
            DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(new FileOutputStream(file)));

            stream.write(header.getBytes());
            stream.write(body.getBytes());

            stream.flush();
            stream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized FullBlock getParent(ContextI context)
    {
        return BlockHeader.FullBlock(getBlockID()-1, context);
    }

    @Override
    public byte[] header() {
        return header.getBytes();
    }

    @Override
    public byte[] content() {
        return body.getBytes();
    }

    @Override
    public void export(SmartDataTransferer smdt) {

    }

    @Override
    public void export(DataOutputStream dost) {

    }

    @Override
    public String toJSON() {
        return null;
    }
}