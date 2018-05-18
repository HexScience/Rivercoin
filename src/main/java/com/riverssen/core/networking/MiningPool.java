package com.riverssen.core.networking;

import com.riverssen.core.chain.BlockData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiningPool implements Runnable
{
    List<Peer> peers;

    public MiningPool()
    {
        peers = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run()
    {
        final long MAX_NUMBER_FOR_NONCE = 0;
        long divisionOfLabour = MAX_NUMBER_FOR_NONCE / peers.size();
        BlockData blockData   = null;

        for(int i = 0; i < peers.size(); i ++)
        {
            peers.get(i).sendStartMineCommand(divisionOfLabour * i, divisionOfLabour, blockData);
        }
    }
}
