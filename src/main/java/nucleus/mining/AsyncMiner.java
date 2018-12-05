package nucleus.mining;

import nucleus.threading.Async;

public class AsyncMiner extends Async<MiningThread>
{
    public void setMinerInstance(MiningThread thread)
    {
        this.asyncronousObject = thread;
    }
}