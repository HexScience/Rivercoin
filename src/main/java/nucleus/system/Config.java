package nucleus.system;

public class Config
{
    private boolean testNetwork;

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
}