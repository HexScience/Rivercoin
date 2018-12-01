package nucleus.protocols;

import nucleus.event.*;
import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;

import java.util.Queue;

public class BlockChain
{
    private Context context;
    private Block   current;

    private Queue<DownloadedBlock> blockQueue;

    public BlockChain(Context context) throws EventFamilyDoesNotExistException
    {
        this.context = context;

        this.context.getEventManager().register((BlockNotificationEventListener) (_BlockNotificationEvent_)->{onEventBlockNotify(_BlockNotificationEvent_); }, "Block");
        this.context.getEventManager().register((RequestedBlockReceivedListener) (_RequestedBlockReceivedEvent_)->{onEventRequestedBlockReceived(_RequestedBlockReceivedEvent_); }, "Block");
    }

    /**
     * @param event
     * This function gets called when a block is sent as a NOTIFICATION
     * most blocks received through this method are within the same blo
     * -ck height of our current block, therefore we check if it's hei
     * -ght is larger than or equal to our current block, never the
     * less, the block is first verified for validity, and then fur
     * -ther checks are done to decide what to do with the block.
     */
    public void onEventBlockNotify(BlockNotificationEvent event)
    {
        long blockHeight = event.getData().getBlock().getHeader().getBlockID();

        if (blockHeight == current.getHeader().getBlockID())
            handleSolvedBlock(event.getData());
        else if (blockHeight > current.getHeader().getBlockID())
            handleFutureBlock(event.getData());
    }

    public void onEventRequestedBlockReceived(RequestedBlockReceivedEvent event)
    {
    }

    private void handleSolvedBlock(DownloadedBlock block)
    {
    }

    private void handleFutureBlock(DownloadedBlock block)
    {
    }

    public void solveBlock()
    {
        current.lock();
    }

    public void run()
    {
    }
}