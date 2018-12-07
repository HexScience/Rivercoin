package com.riverssen.nucleus;

import com.riverssen.nucleus.protocols.protobufs.Address;
import com.riverssen.nucleus.protocols.protobufs.Block;
import com.riverssen.nucleus.protocols.protobufs.Coinbase;

public class GenesisBlock extends Block
{
    public GenesisBlock()
    {
        super();
        getHeader().setVersion(1);
        getHeader().setHeight(0);
        getHeader().setParentHash(new byte[32]);
        getHeader().setMinerAddress(new Address("ANqZngHhZ5iCmJTQSBRkUbG1iLC2pRiCDp"));
        getHeader().setDifficulty(1.978);
        getHeader().setTimeStamp(1544195683241L);
        getHeader().setReward(100_000_000_000L);

        String comment = "December 7 2018, 12:00pm, The Times\n" + "French riot police accused of brutality against schoolchildren.";

        Coinbase coinbase = new Coinbase(getHeader().getReward(), comment, new byte[0], getHeader().getMinerAddress());
        setCoinbase(coinbase);
//        getHeader().setHash(Base58.decode(""));
    }
}