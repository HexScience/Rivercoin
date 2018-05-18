package com.riverssen.core.chain;

import com.riverssen.core.Config;
import com.riverssen.core.tokens.Token;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Serializer;
import com.riverssen.utils.Encodeable;
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

    public BlockData()
    {
        this.merkleTree = new MerkleTree();
    }

    public BlockData(long blockID)
    {
        try
        {
            File file = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + File.separator + "block["+blockID+"]");
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

    public Block toBlock()
    {
        return null;
    }

    public MerkleTree getMerkleTree()
    {
        return merkleTree;
    }

    public boolean transactionsValid()
    {
        List<Token> flat = merkleTree.flatten();
        for(Token token : flat)
            if(!token.isValid()) return false;
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

    public void add(Token token)
    {
        if(!token.isValid()) return;

        merkleTree.add(token);
        dataSize += token.toJSON().getBytes().length;
    }

    public void FetchUTXOs(PublicAddress address, List<com.riverssen.core.headers.UTXO> tokens)
    {
        for(Token token : merkleTree.flatten())
        {

        }
    }
}