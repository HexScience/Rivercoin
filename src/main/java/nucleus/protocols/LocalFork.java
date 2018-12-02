package nucleus.protocols;

import nucleus.protocols.protobufs.Block;
import nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class LocalFork implements ForkI
{
    private Queue<Block> queue;

    public LocalFork()
    {
        this.queue = new SortedLinkedQueue<>();
    }

    @Override
    public boolean add(Block block)
    {
        queue.add(block);
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
        return queue;
    }
}
