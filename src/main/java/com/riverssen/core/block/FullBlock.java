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

import com.riverssen.core.Logger;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.headers.*;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Config;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.core.transactions.RewardTransaction;
import com.riverssen.core.transactions.TransactionOutput;
import com.riverssen.core.utils.*;
import com.riverssen.riverssen.RiverFlowMap;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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

    private RiverFlowMap map;

    public FullBlock(BlockHeader header, BlockData data, BlockHeader parent)
    {
        this.header             = header;
        this.body               = data;
        this.parent             = parent;
        this.map                = new RiverFlowMap();
    }

    public FullBlock(long lastBlock, BlockHeader parent)
    {
        this.header         = new BlockHeader();
        this.body           = new BlockData();
        this.parent         = parent;
        this.header.setBlockID(lastBlock + 1);
        this.map                = new RiverFlowMap();
    }

    public FullBlock(DataInputStream in, ContextI context)
    {
        this.header             = new BlockHeader(in);
        this.body               = new BlockData(in, context);
        if(header.getBlockID() > 0) this.parent = new BlockHeader(header.getBlockID() - 1, context);
        this.map                = new RiverFlowMap();
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

    public synchronized void undoUTXOChanges(ContextI context)
    {
        body.revertOutputs(context);
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
        if (!body.transactionsValid(context)) return err_transactions;

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

    public synchronized void add(TransactionI token, ContextI context)
    {
        body.add(token, context);
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
        Logger.alert("[" + TimeUtil.getPretty("H:M:S") + "][" + header.getBlockID() + "]: new job {"+ algorithm.getClass().getSimpleName() + "{" + algorithm.toString() + "}:" + (difficultyHash) + "}");

        this.body.setTime(System.currentTimeMillis());
        body.getMerkleTree().buildTree();

        //System.out.println(new RewardTransaction(miner).toJSON());

        /** add the reward BEFORE mining the block **/
        body.addNoValidation(new RewardTransaction(miner, algorithm.encode(body.getBytes())), context);
        /** rebuild tree to include reward **/
        body.getMerkleTree().buildTree();

        header.setVersion(context.getVersionBytes());
        header.setParentHash(parentHash);
        header.setMerkleRoot(body.getMerkleTree().encode(ByteUtil.defaultEncoder()));
        header.setRiverMerkleRoot(body.getMerkleTree().encode(ByteUtil.defaultEncoder()));
        header.setTimeStamp(body.getTimeStamp());
        header.setDifficulty(difficulty);
        header.setMiner(context.getMiner());
        header.setReward(new RiverCoin(Config.getReward()));

        ByteBuffer data = getBodyAsByteBuffer();

        long nonce = 0;
        this.hash = algorithm.encode16(data.array());

        while (new BigInteger(hash, 16).compareTo(difficulty) >= 0) { data.putLong(data.capacity() - 8, ++nonce); this.hash = algorithm.encode16(data.array()); }

        header.setHash(algorithm.encode(data.array()));
        header.setNonce(nonce);
        this.hash = header.getHashAsString();

        double time = (System.currentTimeMillis() - this.body.getTimeStamp()) / 1000.0;
        Logger.alert("[" + TimeUtil.getPretty("H:M:S") + "][" + header.getBlockID() + "]: hashing took '" + time + "s' '" + this.hash + "'");

        /** Send Solution To Nodes **/

        context.getNetworkManager().sendBlock(this);
    }

    public synchronized BlockHeader getHeader()
    {
        return header;
    }

    @Override
    public byte[] getBytes()
    {
        return ByteUtil.concatenate(this.parent != null ? this.parent.getHash() : new byte[32], body.getBytes());
    }

    public synchronized BlockData getBody()
    {
        return body;
    }

    private synchronized ByteBuffer getBodyAsByteBuffer()
    {
        byte bodydata[] = getBytes();
        ByteBuffer data = ByteBuffer.allocate(32 + 32 + PublicAddress.SIZE + 32 + 8 + 8 + 32 + RiverCoin.MAX_BYTES + bodydata.length + 8);
        data.put(header.getParentHash());
        data.put(header.getDifficulty());
        data.put(header.getMinerAddress());
        data.put(header.getMerkleRoot());
        data.putLong(header.getBlockID());
        data.putLong(header.getVersion());
        data.put(header.getrvcRoot());
        data.put(header.getReward());
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

            header.export(stream);
            body.export(stream);

            stream.flush();
            stream.close();

            LatestBlockInfo info = new LatestBlockInfo(context.getConfig());
            info.read();

            info.write(getBlockID(), info.getLastBlockCheck(), info.getLastBlockCheckTimestamp(), info.getDifficulty(), info.getTotalHashes());
        } catch (Exception e)
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
        return new JSON().add(header.toString())
                .add(new JSON("outputs", true).add(outputsAsJSON()).toString())
                .add(new JSON("body").add(body.getMerkleTree().toString()).toString()).toString();
    }

    private String outputsAsJSON() {
        String data = "";

        for(TransactionOutput output : getBody().getOutputs())
            data += output.toString() + ", ";

        if (data.length() == 0) return "";

        return data.substring(0, data.length() - 2);
    }
}