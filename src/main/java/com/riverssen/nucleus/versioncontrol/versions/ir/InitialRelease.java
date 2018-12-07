package com.riverssen.nucleus.versioncontrol.versions.ir;

import com.riverssen.nucleus.versioncontrol.Constructors;
import com.riverssen.nucleus.versioncontrol.Version;

public class InitialRelease extends Version
{
    public InitialRelease()
    {
        super(new Constructors(new irBlockHeaderConstructor(), new irBlockConstructor(), new irTransactionConstructor(), new irTransactionInputConstructor(), new irTransactionOutputConstructor()), 0);
    }
}