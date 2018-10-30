package nucleus.exceptions;

public class PointDecompressionException extends ECLibException
{
    public PointDecompressionException()
    {
        super("PointDecompressionException: ECPoint cannon be reconstructed from compressed bytes.");
    }
}
