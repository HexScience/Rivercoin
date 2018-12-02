package nucleus.protocols;

import nucleus.net.server.IpAddress;
import nucleus.protocols.protobufs.Block;
import nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class Fork implements ForkI
{
    private Queue<Block>    blockQueue;
    private IpAddress       peer;

    @Override
    public void add(Block block)
    {
    }

    public void add(DownloadedBlock downloadedBlock)
    {
        if (peer == null)
        {
            peer = downloadedBlock.getSender();
            blockQueue = new SortedLinkedQueue<>();
            blockQueue.add(downloadedBlock.getBlock());
        }
        else if (downloadedBlock.getSender().equals(peer))
            blockQueue.add(downloadedBlock.getBlock());
    }

    @Override
    public Queue<Block> get()
    {
        return blockQueue;
    }
}