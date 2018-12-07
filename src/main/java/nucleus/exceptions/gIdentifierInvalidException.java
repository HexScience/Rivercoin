package nucleus.exceptions;

public class gIdentifierInvalidException extends Throwable
{
    public gIdentifierInvalidException(String data)
    {
        super("identifier '" + data + "' is invalid.");
    }
}