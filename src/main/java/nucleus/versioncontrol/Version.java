package nucleus.versioncontrol;

import nucleus.util.ByteUtil;
import nucleus.versioncontrol.versions.ir.InitialRelease;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Version
{
    private static final Map<Integer, VersionLoader> versionLoaders = new LinkedHashMap<>();

    public static Version getInstance(String version)
    {
        return getInstance(Format(version));
    }

    public static Version getInstance(int version)
    {
        return null;
    }

    public static int Format(String version)
    {
        byte bytes[]            = new byte[4];

        String version_format   = "xxx.xxx.xxx.%w";
        String data[]           = version.split("\\.");

        bytes[0]                = (byte) Integer.parseInt(data[0]);
        bytes[1]                = (byte) Integer.parseInt(data[1]);
        bytes[2]                = (byte) Integer.parseInt(data[2]);
        bytes[3]                = (byte) Integer.parseInt(data[3]);

        int result              = ByteUtil.decodei(bytes);

        return Math.abs(result);
    }

    public static final void init()
    {
        versionLoaders.put(Format("0.0.1.0"), ()->{return new InitialRelease(); });
    }

    public static Version getLatest()
    {
        VersionLoader versionLoader = null;

        for (VersionLoader v : versionLoaders.values())
            versionLoader = v;

        return versionLoader.load();
    }

    private Constructors constructors;

    protected Version(Constructors constructors)
    {
        this.constructors = constructors;
    }

    public Constructors getConstructors()
    {
        return constructors;
    }
}