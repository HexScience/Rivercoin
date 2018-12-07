package nucleus.gtp;

import nucleus.exceptions.gIdentifierInvalidException;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;

import static nucleus.util.HashUtil.applySha256;
import static nucleus.util.HashUtil.applySha512;

public class gIdentifier
{
    private byte identifier[];

    public gIdentifier(byte[] fullData)
    {
        this.identifier = ByteUtil.concatenate(ByteUtil.encode(fullData.length), applySha512(applySha512(fullData)));
        this.identifier = ByteUtil.concatenate(identifier, ByteUtil.trim(applySha256(identifier), 0, 4));
    }

    public gIdentifier(String identifier) throws gIdentifierInvalidException
    {
        byte[] bidentifer = Base58.decode(identifier);

        if (bidentifer.length != 76)
            throw new gIdentifierInvalidException(identifier);

        if (!ByteUtil.equals(ByteUtil.trim(applySha256(ByteUtil.trim(bidentifer, 0, 72)), 0, 4), ByteUtil.trim(bidentifer, 72, 76)))
            throw new gIdentifierInvalidException(identifier);

        this.identifier = bidentifer;
    }

    public byte[] getIdentifier()
    {
        return identifier;
    }

    @Override
    public String toString()
    {
        return Base58.encode(identifier);
    }
}