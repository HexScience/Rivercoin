package nucleus.consensys;

import nucleus.protocols.protobufs.Block;
import nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class LocalFork extends ForkI
{
    public LocalFork()
    {
        this.blockQueue = new SortedLinkedQueue<>();
    }

    @Override
    public boolean add(Block block)
    {
        blockQueue.add(block);
        return true;
    }

    @Override
    public boolean add(DownloadedBlock block)
    {
        return false;
    }

    @Override
    public Queue<Block> get()
    {
        return blockQueue;
    }
}
