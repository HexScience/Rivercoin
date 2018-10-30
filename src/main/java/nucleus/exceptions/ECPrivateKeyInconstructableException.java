package nucleus.exceptions;

public class ECPrivateKeyInconstructableException extends ECLibException
{
    public ECPrivateKeyInconstructableException(String bytesNumber)
    {
        super("ECPrivateKeyInconstructableException: private key cannot be constructed from given bytes (" + bytesNumber + ").");
    }
}
