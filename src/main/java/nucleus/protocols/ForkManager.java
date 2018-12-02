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
    }
}