package nucleus.web.addressing;

import nucleus.crypto.ec.ECDerivedPublicKey;
import nucleus.exceptions.ECLibException;
import nucleus.protocol.protobufs.Address;
import nucleus.util.ByteUtil;

public class MnemonicAddress
{
    private Address address = new Address();
    private byte[]  mnemonc = new byte[256];
    private byte[]  describ = new byte[0];
    private byte[]  sigfutr = new byte[71];
    private byte[]  unlockc = new byte[0];

    public void changeInformation(Address forwardingAddress, byte[] mnemonicAddress, byte[] description, byte[] sigfutr, byte[] unlockc,
                                  ECDerivedPublicKey secretPubKey, byte[] secret)
    {
        try
        {
            if (secretPubKey.verify(sigfutr, ByteUtil.concatenate(this.bytes())));
        } catch (ECLibException e)
        {
            e.printStackTrace();
        }
        //TODO: Change info
    }

    public byte[] bytes()
    {
        return null;
    }
}
