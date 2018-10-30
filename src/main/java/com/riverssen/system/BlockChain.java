package com.riverssen.system;

import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.block.FullBlock;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.system.Logger;

import java.util.HashMap;
import java.util.Map;

public class BlockChain implements Runnable
{
    /** A download queue to contain all incoming blocks **/
    private DownloadQue<FullBlock>      blockQueMap;
    /** A download queue to contain all incoming requested headers **/
    private DownloadQue<BlockHeader>    headerQueMap;
    private ContextI                    context;
    private FullBlock                   currentFork;
    private long                        currentSize;
    private long                        currentlyBehind;

    public BlockChain()
    {
        blockQueMap     = new DownloadQue<>();
        headerQueMap    = new DownloadQue();
    }

    public void queBlock(FullBlock block, String address)
    {
        blockQueMap.add(block, address);
    }

    public void queHeader(BlockHeader header, String address)
    {
        headerQueMap.add(header, address);
    }

    public void setCurrentlyBehind(long amount)
    {
        long number = amount - currentSize;
        if (number < 0) return;

        this.currentlyBehind = Math.max(number, this.currentlyBehind);
        Logger.alert("Current chain is currently behind by " + currentlyBehind + " blocks.");
    }

    private boolean confirm(DownloadQue<BlockHeader>.QueElement<BlockHeader> longChain)
    {
        return false;
    }

    private void catchup(String longChain)
    {
        /**
         * send request to peer to send full chain blocks.
         */

        /** confirm received blocks **/

        /**
         * catch up.
         */
    }

    private void checkForForks()
    {
        if (headerQueMap.size() > 0)
        {
            DownloadQue<BlockHeader>.QueElement<BlockHeader> longestHeaderChain = headerQueMap.getLongest();

            if (confirm(longestHeaderChain))
                catchup(longestHeaderChain.address);
        }
    }

    @Override
    public void run()
    {
        while (context.isRunning())
        {
        }
    }
}