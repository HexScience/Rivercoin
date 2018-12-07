package com.riverssen.nucleus.system;

import com.riverssen.nucleus.protocols.protobufs.Address;

public class Config
{
    private boolean testNetwork;
    private Address miner;

    public boolean getIsTestNetwork()
    {
        return testNetwork;
    }

    public short getPort()
    {
        if (testNetwork)
            return Parameters.TEST_NETWORK_NODE_PORT;
        else
            return Parameters.MAIN_NETWORK_NODE_PORT;
    }

    public Address getMiner()
    {
        return miner;
    }
}