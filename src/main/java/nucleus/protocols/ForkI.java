package nucleus.protocols;

import nucleus.protocols.protobufs.Block;

import java.util.Queue;

public interface ForkI
{
    void add(Block block);
    void add(DownloadedBlock block);
    Queue<Block> get();
}