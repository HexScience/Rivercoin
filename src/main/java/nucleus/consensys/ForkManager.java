package nucleus.consensys;

import nucleus.net.server.IpAddress;
import nucleus.protocols.protobufs.Block;

import java.util.LinkedHashSet;
import java.util.Set;

public class ForkManager
{
    private Set<ForkI>  forks;
    private ForkI       main;

    public ForkManager()
    {
        this.forks = new LinkedHashSet<>();
    }

    public void add(DownloadedBlock block)
    {
        for (ForkI fork : forks)
            if (fork.add(block))
                return;

        ForkI fork = null;
        forks.add(fork = new ForeignFork());
        fork.add(block);

        checkShouldAdd(block.getBlock());
    }

    public void add(Block block)
    {
        for (ForkI fork : forks)
            if (fork.add(block))
                return;

        ForkI fork = null;
        forks.add(fork = new ForeignFork());
        fork.add(block);

        checkShouldAdd(block);
    }

    private void checkShouldAdd(Block block)
    {
        Block existing      = main.getAt(block.getHeader().getBlockID());
        boolean blockExists = existing != null;

        if (!blockExists)
            main.add(block);
        else{
            Block preexstng = main.getAt(block.getHeader().getBlockID());
            long eTimeAvge  = existing.getHeader().getTimeStamp() - preexstng.getHeader().getTimeStamp();
            long nTimeAvge  = block.getHeader().getTimeStamp() - preexstng.getHeader().getTimeStamp();

            double eRatio   = existing.ratio();
            double nRatio   = block.ratio();

            if (!(eTimeAvge < nTimeAvge && eRatio > nRatio && existing.getTotalValue() > block.getTotalValue()))
                main.replace(block.getHeader().getBlockID(), block);
        }
    }

    public ForkI getMain()
    {
        return main;
    }

    public ForkI get(IpAddress sender)
    {
        for (ForkI forkI : forks)
            if (forkI.hasSender(sender))
                return forkI;

        return null;
    }
}