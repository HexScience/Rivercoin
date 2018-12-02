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
    public void add(Block block)
    {
        queue.add(block);
    }

    @Override
    public void add(DownloadedBlock block)
    {
    }

    @Override
    public Queue<Block> get()
    {
        return queue;
    }
}
