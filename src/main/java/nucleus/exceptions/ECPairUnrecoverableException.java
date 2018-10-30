package nucleus.exceptions;

public class ECPairUnrecoverableException extends ECLibException
{
    public ECPairUnrecoverableException(String info)
    {
        super("ECPairUnrecoverableException: " + info + ".");
    }
}
