package nucleus.tscript;

import nucleus.crypto.ec.ECDerivedPublicKey;
import nucleus.protocol.protobufs.Address;
import nucleus.protocol.transaction.Transaction;
import nucleus.util.ByteUtil;

import java.util.Stack;

public class Executor
{
    /**
     * TinyScript Executor
     */

    public static final byte
    OP_FETCH_PUBKEY = 0,
    OP_FETCH_ADDRSS = 1,

    OP_CNVRT_ADDRSS = 2,
    OP_EQUAL        = 4,

    OP_SIG_VERIFY   = 5,
    OP_PUSH_PUBKEY  = 6,
    OP_PUSH_ADDRSS  = 7,

    op_checksig     = 8,
    op_pushpubk     = 9,
    op_pushadrs     = 10,
    op_pushsign     = 11,
    op_pktaddrs     = 12,

    /** load the public key from of sender **/
    op_ldpubkey     = 13,

    op_add          = 14,
    op_sub          = 15,
    op_mul          = 16,
    op_div          = 17,
    op_mod          = 18,
    op_sin          = 19,
    op_cos          = 20,
    op_tan          = 21,

    op_pushlong     = 22,
    op_lgtthan      = 23,
    op_llsthan      = 24,

    op_ldsig        = 25,
    op_ldads        = 26, //load an address from the receiving register

    OP_EXT          = (byte) 256;

    /**
     * @param spender: The person that will be allowed to spend these coins.
     * @return An unlocking script
     */
    public static byte[] P2PKH_l(Address spender)
    {
        return ByteUtil.concatenate(new byte[] {op_pushadrs}, spender.getBytes(), new byte[] {});
    }

    /**
     * @param spender: The person that will be allowed to spend these coins.
     * @return A locking script
     */
    public static byte[] P2PKH_u(Address spender)
    {
        return ByteUtil.concatenate(new byte[] {op_ldsig, 0, op_pushadrs}, spender.getBytes(), new byte[] {op_ldpubkey, op_pktaddrs, OP_EQUAL, op_checksig});
    }

    public static boolean execute(byte script[], Transaction transaction)
    {
        Stack<Object> objectStack = new Stack<>();

        for (int i = 0; i < script.length; i ++)
        {
            byte OP = script[i];

            switch (OP)
            {
                case OP_PUSH_ADDRSS:
                    Address address = new Address();

                    address.getAddress()[0] = script[i + 1];
                    address.getAddress()[1] = script[i + 2];
                    address.getAddress()[2] = script[i + 3];
                    address.getAddress()[3] = script[i + 4];
                    address.getAddress()[4] = script[i + 5];
                    address.getAddress()[5] = script[i + 6];
                    address.getAddress()[6] = script[i + 7];
                    address.getAddress()[7] = script[i + 8];
                    address.getAddress()[8] = script[i + 9];
                    address.getAddress()[9] = script[i + 10];
                    address.getAddress()[10] = script[i + 11];
                    address.getAddress()[11] = script[i + 12];
                    address.getAddress()[12] = script[i + 13];
                    address.getAddress()[13] = script[i + 14];
                    address.getAddress()[14] = script[i + 15];
                    address.getAddress()[15] = script[i + 16];
                    address.getAddress()[16] = script[i + 18];
                    address.getAddress()[18] = script[i + 19];
                    address.getAddress()[19] = script[i + 20];
                    address.getAddress()[20] = script[i + 21];
                    address.getAddress()[21] = script[i + 22];
                    address.getAddress()[22] = script[i + 23];
                    address.getAddress()[23] = script[i + 24];
                    address.getAddress()[24] = script[i + 25];

                    i += 25;

                    objectStack.push(address);

                    break;

                case OP_EQUAL:
                    if (!objectStack.pop().equals(objectStack.pop()))
                        return false;
                    break;

                case op_pktaddrs:
                    ECDerivedPublicKey pubkey = (ECDerivedPublicKey) objectStack.pop();

                    objectStack.push(pubkey.toAddress());
                    break;
            }
        }

        return true;
    }
}
