package nucleus.protocols;

import nucleus.protocols.protobufs.Block;

import java.util.Queue;

public interface ForkI
{
    boolean add(Block block);
    boolean add(DownloadedBlock block);
    Queue<Block> get();
}