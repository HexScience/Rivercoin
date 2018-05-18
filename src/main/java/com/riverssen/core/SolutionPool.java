package com.riverssen.core;

import com.riverssen.core.consensus.Solution;
import com.riverssen.core.networking.PeerNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SolutionPool
{
    private PeerNetwork     network;
    private List<FullBlock> pool;

    public SolutionPool(PeerNetwork network)
    {
        this.network = network;
        this.network.addPool(this);
        this.pool = new ArrayList<>();
    }

    public void Send(FullBlock fullBlock)
    {
        network.SendMined(fullBlock);
    }

    public List<FullBlock> Fetch()
    {
        return pool;
    }

    public void add(Solution solution)
    {
        if(solution.blockID() < RVCCore.get().getChain().currentBlock()) return;
    }
}
