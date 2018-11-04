package nucleus.exceptions;

public class PayLoadException extends Exception
{
    public PayLoadException(String data)
    {
        super("PayLoadException: " + data + ".");
    }
}
