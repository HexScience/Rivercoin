package nucleus.exceptions;

public class InvalidWalletFileException extends Throwable
{
    public InvalidWalletFileException()
    {
        super("invalid wallet file provided.");
    }
}