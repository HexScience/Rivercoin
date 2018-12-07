package com.riverssen.nucleus.versioncontrol;

import com.riverssen.nucleus.util.Logger;

public class VersionControl
{
    private static final Logger Logger = com.riverssen.nucleus.util.Logger.get("VersionControl");
    public static final void init()
    {
        Version.init();

        Logger.alert("VersionControl initialized successfully.");
    }
}