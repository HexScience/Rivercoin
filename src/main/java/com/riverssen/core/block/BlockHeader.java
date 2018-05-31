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

import com.riverssen.core.FullBlock;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.system.Context;
import com.riverssen.utils.ByteUtil;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.utils.MerkleTree;

import java.io.*;
import java.math.BigInteger;
import java.util.zip.InflaterInputStream;

public class BlockHeader implements Encodeable
{
    public static long SIZE = 32 + 32 + 32 + 8 + 32 + 20 + 8 + 8;
    /** 2 byte version information **/
    private final byte version[] = new byte[2];
    /** 32 byte hash of **/
    private final byte hash[] = new byte[32];
    /** 32 byte hash of the block **/
    private final byte parentHash[] = new byte[32];
    /** 32 byte hash of the merkle root **/
    private final byte merkleRoot[] = new byte[32];
    /** 32 byte hash of the UTXOChainmerkle root **/
    private final byte riverMerkleRoot[] = new byte[32];
    /** 8 byte integer of the blocks timestamp **/
    private final byte timeStamp[] = new byte[8];
    /** 32 byte integer of the difficulty **/
    private final byte difficulty[] = new byte[32];
    /** 20 byte hash of the miners public address **/
    private final byte minerAddress[] = new byte[20];
    /** 8 byte integer solution **/
    private final byte nonce[] = new byte[8];
    /** 8 byte integer referencing the block id **/
    private final byte blockID[] = new byte[8];

    public BlockHeader(String hash, String parentHash, String merkleRoot, MerkleTree tree, long timeStamp, BigInteger difficulty, PublicAddress minerAddress, long nonce)
    {
        this(null, null, null, null, null, null, null, null);
    }

    public BlockHeader(byte[] hash, byte[] parentHash, byte[] merkleRoot, byte[] merkleTree, byte[] timeStamp, byte[] difficulty, byte[] minerAddress, byte[] nonce)
    {
    }

    public BlockHeader()
    {
    }

    public BlockHeader(long block, Context context)
    {
        if(block < 0) return;

        File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block["+block+"]");

        try
        {
            DataInputStream stream = new DataInputStream(new InflaterInputStream(new FileInputStream(file)));

            stream.read(version);
            stream.read(hash);
            stream.read(parentHash);
            stream.read(merkleRoot);
            stream.read(timeStamp);
            stream.read(difficulty);
            stream.read(minerAddress);
            stream.read(nonce);
            stream.read(blockID);

            stream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public BlockHeader(DataInputStream stream)
    {
        try
        {
            stream.read(version);
            stream.read(hash);
            stream.read(parentHash);
            stream.read(merkleRoot);
            stream.read(timeStamp);
            stream.read(difficulty);
            stream.read(minerAddress);
            stream.read(nonce);
            stream.read(blockID);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public byte[] getHash()
    {
        return hash;
    }

    public byte[] getParentHash()
    {
        return parentHash;
    }

    public byte[] getMerkleRoot()
    {
        return merkleRoot;
    }

    public byte[] getTimeStamp()
    {
        return timeStamp;
    }

    public byte[] getDifficulty()
    {
        return difficulty;
    }

    public byte[] getMinerAddress()
    {
        return minerAddress;
    }

    public byte[] getNonce()
    {
        return nonce;
    }

    public String getHashAsString()
    {
        return ByteUtil.decodeHash(this.hash);
    }

    public long getBlockID()
    {
        return ByteUtil.decode(blockID);
    }

    public String getParentHashAsString()
    {
        return ByteUtil.decodeHash(getParentHash());
    }

    public String getMerkleRootAsString()
    {
        return ByteUtil.decodeHash(getMerkleRoot());
    }

    public long getTimeStampAsLong()
    {
        return ByteUtil.decode(getTimeStamp());
    }

    public BigInteger getDifficultyAsInt()
    {
        return new BigInteger(getDifficulty());
    }

    public long getNonceAsInt()
    {
        return ByteUtil.decode(getNonce());
    }

    public PublicAddress getMinerAddressAsPublicAddress()
    {
        return new PublicAddress(getMinerAddress());
    }

    public void setHash(byte[] hash)
    {
        for(int i = 0; i < 32; i ++) this.hash[i] = hash[i];
    }

    public void setParentHash(byte[] hash)
    {
        for(int i = 0; i < 32; i ++) this.parentHash[i] = hash[i];
    }

    public void setMerkleRoot(byte root[])
    {
        for(int i = 0; i < 32; i ++) this.merkleRoot[i] = root[i];
    }

    public void setTimeStamp(long serializable)
    {
        timeStamp[0] = (byte) (serializable >> 56);
        timeStamp[1] = (byte) (serializable >> 48);
        timeStamp[2] = (byte) (serializable >> 40);
        timeStamp[3] = (byte) (serializable >> 32);
        timeStamp[4] = (byte) (serializable >> 24);
        timeStamp[5] = (byte) (serializable >> 16);
        timeStamp[6] = (byte) (serializable >> 8);
        timeStamp[7] = (byte) serializable;
    }

    @Override
    public byte[] getBytes()
    {
        return ByteUtil.concatenate(version, hash, parentHash, merkleRoot, timeStamp, difficulty, minerAddress, nonce, blockID);
    }

    public void setDifficulty(BigInteger difficulty)
    {
        System.arraycopy(difficulty.toByteArray(), 0, this.difficulty, 32 - difficulty.toByteArray().length, difficulty.toByteArray().length);
    }

    public void setNonce(long nonce)
    {
        System.arraycopy(this.nonce, 0, ByteUtil.encode(nonce), 0, 8);
    }

    public void setMinerAddress(byte[] minerAddress)
    {
        System.arraycopy(minerAddress, 0, this.minerAddress, 0, this.minerAddress.length);
    }

    public void set(BlockHeader header)
    {
    }

    public void setBlockID(long blockID)
    {
        this.blockID[0] = (byte) (blockID >> 56);
        this.blockID[1] = (byte) (blockID >> 48);
        this.blockID[2] = (byte) (blockID >> 40);
        this.blockID[3] = (byte) (blockID >> 32);
        this.blockID[4] = (byte) (blockID >> 24);
        this.blockID[5] = (byte) (blockID >> 16);
        this.blockID[6] = (byte) (blockID >> 8);
        this.blockID[7] = (byte)  blockID;
    }

    public FullBlock continueChain()
    {
        return new FullBlock(getBlockID() + 1, this);
    }

    public static FullBlock FullBlock(long block, Context context)
    {
        File file = new File(context.getConfig().getBlockChainDirectory() + File.separator + "block["+block+"]");
        DataInputStream stream = null;
        try
        {
            stream = new DataInputStream(new InflaterInputStream(new FileInputStream(file)));
            BlockHeader header = new BlockHeader(stream);
            BlockHeader parent = new BlockHeader(header.getBlockID(), context);
            FullBlock fBlock = new FullBlock(header, new BlockData(stream), parent);

            stream.close();

            return fBlock;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}