package nucleus.versioncontrol;

import nucleus.util.Logger;

public class VersionControl
{
    public static final void init()
    {
        Version.init();

        Logger.alert("VersionControl initialized successfully.");
    }
}