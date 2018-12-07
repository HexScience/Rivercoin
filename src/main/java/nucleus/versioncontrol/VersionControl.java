package nucleus.versioncontrol;

import nucleus.util.Logger;

public class VersionControl
{
    private static final Logger Logger = nucleus.util.Logger.get("VersionControl");
    public static final void init()
    {
        Version.init();

        Logger.alert("VersionControl initialized successfully.");
    }
}