package nucleus.versioncontrol.versions.ir;

import nucleus.versioncontrol.Constructors;
import nucleus.versioncontrol.Version;

public class InitialRelease extends Version
{
    public InitialRelease()
    {
        super(new Constructors(new irBlockHeaderConstructor(), new irBlockConstructor(), new irTransactionConstructor(), new irTransactionInputConstructor(), new irTransactionOutputConstructor()));
    }
}