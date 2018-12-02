package nucleus.protocols;

import nucleus.protocols.protobufs.Block;

import java.util.LinkedHashSet;
import java.util.Set;

public class ForkManager
{
    private Set<ForkI> forks;

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
    }

    public void add(Block block)
    {
        for (ForkI fork : forks)
            if (fork.add(block))
                return;

        ForkI fork = null;
        forks.add(fork = new ForeignFork());
        fork.add(block);
    }
}