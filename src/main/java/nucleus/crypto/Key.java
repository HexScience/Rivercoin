package nucleus.crypto;

import nucleus.exceptions.ECLibException;

public interface Key
{
    public boolean  fromBytes(byte bytes[]);
    public byte[]   getBytes();
    public default byte[]   sign(byte bytes[])
    {
        return sign(bytes, null);
    }
    public byte[]   sign(byte bytes[], byte encryption[]);
    public boolean  verify(byte signature[], byte data[]) throws ECLibException;
    public boolean  decrypt(byte key);
    public boolean  encrypt(byte key);

    Key getCompressedForm();
    Key getUncompressedForm();
    Key getAddressForm(byte prefix);
}
