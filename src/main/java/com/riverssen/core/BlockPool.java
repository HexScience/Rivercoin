/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core;

import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.networking.Peer;
import com.riverssen.core.networking.NetworkManager;
import com.riverssen.core.system.Context;
import com.riverssen.core.system.LatestBlockInfo;

import java.util.*;

public class BlockPool
{
    private final HashMap<Peer, Long> chainSizes = new HashMap<>();
    private boolean             loading;
    private Context             network;
    private List<FullBlock>     blocks;
    private LatestBlockInfo     lbi;

    public BlockPool(Context network)
    {
        this.network    = network;
        blocks          = Collections.synchronizedList(new ArrayList<>());
        lbi             = new LatestBlockInfo();
        try {
            lbi.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.loading    = true;
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

            if(chainSizes.get(peer).compareTo(pl) >= 0){
                p=peer;
                pl=chainSizes.get(peer);
            }
        }

        return p;
    }

    private void load()
    {
        long chainsize = network.getNetworkManager().GetChainSize();

        if(chainsize > lbi.getLatestBlock())
        {
            List<FullBlock> pool = new ArrayList<>();
            network.getNetworkManager().fetchAllBlocks(pool, lbi.getLatestBlock());

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
        network.getNetworkManager().SendMined(fullBlock);
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
