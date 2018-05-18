package com.riverssen.core;

import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.networking.PeerNetwork;
import com.riverssen.core.system.LatestBlockInfo;

import java.util.*;

public class BlockPool
{
    private final HashMap<Peer, Long> chainSizes = new HashMap<>();
    private boolean             loading;
    private PeerNetwork         network;
    private List<FullBlock>     blocks;
    private LatestBlockInfo     lbi;

    BlockPool(PeerNetwork network) throws Exception
    {
        this.network    = network;
        blocks          = Collections.synchronizedList(new ArrayList<>());
        lbi             = new LatestBlockInfo();
        lbi.read();
        this.loading    = true;
        this.network.addPool(this);
    }

    private Peer getBiggestChain()
    {
        Peer p = null;
        Long pl = null;

        for(Peer peer : chainSizes.keySet())
        {
            if(p == null)
            {
                p = peer;
                pl = chainSizes.get(pl);
                continue;
            }

            if()
        }

        return p;
    }

    private void load()
    {
        long chainsize = network.GetChainSize();

        if(chainsize > lbi.getLatestBlock())
        {
            List<FullBlock> pool = new ArrayList<>();
            network.fetchAllBlocks(pool, lbi.getLatestBlock());

            Set<String> blocks = new HashSet<>();
            FullBlock header = lbi.getLatestFullBlock();

            if(header != null)
                blocks.add(header.getHashAsString());

            for(FullBlock unverified : pool)
            {
                String hash = unverified.getHashAsString();

                if(blocks.contains(hash)) continue;

                long headerID = header == null ? - 1 : header.getBlockID();

                if(headerID >= unverified.getBlockID()) continue;

                int err = unverified.validate(header.getHeader());

                if(err > 0) continue;

                blocks.add(hash);
                this.blocks.add(unverified);

                header = unverified;
            }
        }

        loading        = false;
    }

    public List<FullBlock> Fetch()
    {
        load();
        return blocks;
    }

    public boolean NotFull()
    {
        return false;
    }

    public boolean Empty()
    {
        return blocks.isEmpty();
    }

    public void Send(FullBlock fullBlock)
    {
        network.SendMined(fullBlock);
    }

    public void add(FullBlock receive)
    {
        /** if its an old block then don't add it **/
        if(receive.getBlockID() <= RVCCore.get().getChain().currentBlock()) return;

        BlockHeader parent = null;
        if(blocks.size() > 0) parent = blocks.get(blocks.size() - 1).getHeader();
        else parent = new BlockHeader(receive.getBlockID() - 1);

        if(receive.validate() == 0) blocks.add(receive);
    }
}
