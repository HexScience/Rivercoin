package nucleus.consensys;

import nucleus.net.server.IpAddress;
import nucleus.protocols.protobufs.Block;
import nucleus.util.SortedLinkedQueue;

import java.util.Queue;

public class ForeignFork extends ForkI
{
    private IpAddress       peer;

    @Override
    public boolean add(Block block)
    {
        return false;
    }

    public boolean add(DownloadedBlock downloadedBlock)
    {
        if (peer == null)
        {
            peer = downloadedBlock.getSender();
            blockQueue = new SortedLinkedQueue<>();
            blockQueue.add(downloadedBlock.getBlock());
        }
        else if (downloadedBlock.getSender().equals(peer))
            blockQueue.add(downloadedBlock.getBlock());

        return peer.equals(downloadedBlock.getSender());
    }

    @Override
    public Queue<Block> get()
    {
        return blockQueue;
    }
}