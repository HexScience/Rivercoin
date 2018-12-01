package nucleus.exceptions;

public class MXTypeNotSupportedException extends Throwable
{
    public MXTypeNotSupportedException(String type)
    {
        super("type '" + type + "' not supported.");
    }
}