package com.riverssen.nucleus.versioncontrol.versions.ir;

import com.riverssen.nucleus.mining.Nonce;
import com.riverssen.nucleus.protocols.protobufs.BlockHeader;
import com.riverssen.nucleus.protocols.protobufs.CompressedKey;
import com.riverssen.nucleus.versioncontrol.Constructor;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class irBlockHeaderConstructor implements Constructor<BlockHeader>
{
    @Override
    public BlockHeader ConstructFromBytes(ByteBuffer data)
    {
        BlockHeader header = new BlockHeader();
        byte hash[] = new byte[32];
        byte pubk[] = new byte[32];

        header.setVersion(data.getLong());
        header.setBlockID(data.getLong());

        data.get(hash); header.setHash(hash);
        data.get(hash); header.setParentHash(hash);
        data.get(hash); header.setAcceptedMerkleRoot(hash);
        data.get(hash); header.setRejectedMerkleRoot(hash);
        header.setTimeStamp(data.getLong());
        header.setDifficulty(data.getDouble());
        byte nonce[] = new byte[24]; data.get(nonce); header.setNonce(new Nonce(nonce));
        data.get(pubk); header.setMinerAddress(new CompressedKey(pubk));
        header.setReward(data.getLong());

        return header;
    }

    @Override
    public BlockHeader ConstructFromInput(DataInputStream stream) throws IOException
    {
//        BlockHeader header = new BlockHeader();
//        byte hash[] = new byte[32];
//        byte pubk[] = new byte[32];
//
//        header.setVersion(stream.readLong());
//        header.setBlockID(stream.readLong());
//
//        stream.read(hash); header.setParentHash(hash);
//        stream.read(hash); header.setMerkleRoot(hash);
//        stream.read(hash); header.setForkRoot(hash);
//        header.setTimeStamp(stream.readLong());
//        header.setDifficulty(stream.readDouble());
//        header.setNonce(stream.readLong());
//        stream.read(pubk); header.setMinerAddress(new CompressedKey(pubk));
//        header.setReward(stream.readLong());
//
//        return header;
        byte bytes[] = new byte[BlockHeader.SIZE];

        stream.read(bytes);
        return ConstructFromBytes(bytes);
    }

    @Override
    public BlockHeader ConstructFromOther(BlockHeader other)
    {
        return null;
    }
}