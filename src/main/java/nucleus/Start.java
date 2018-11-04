package nucleus;

import nucleus.crypto.ec.ECLib;
import nucleus.exceptions.ECLibException;
import nucleus.system.Context;

public class Start
{
    public static void main(String args[]) throws ECLibException
    {
        ECLib.init();
        Context context = new NucleusContext();
    }
}