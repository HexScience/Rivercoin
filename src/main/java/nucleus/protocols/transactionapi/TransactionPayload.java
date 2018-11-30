package nucleus.protocols.transactionapi;

import nucleus.crypto.ec.ECDerivedPublicKey;
import nucleus.exceptions.ECLibException;
import nucleus.exceptions.PayLoadException;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transaction.Signature;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;
import nucleus.util.HashUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Stack;

public class TransactionPayload
{
    public static enum Op
    {
        PUBKEY,
        ADDRSS,
        LONG,
        DOUBLE,

        CHECKSIG,
        PK2ADDRS,

        /**
         * Math operations
         */
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,
        SIN,
        COS,
        TAN,

        CAST_INTGR,

        HASADDRS,
        HASPUBKY,
        HASSIGNR,

        PUSH_L,
        PUSH_D,
        PUSH_PK,
        PUSH_AD,
        PUSH_PRK,
        PUSH_DATA,
        PUSH_SIG,
        DUP,
        DUP2,
        DUP3,
        DUP4,
        DUP5,
        DUP6,
        POP,
        POP2,
        POP3,
        POP4,
        POP5,
        POP6,
        ROT,
        ROTSWAP,
        ROT2,
        ROTSWAP2,
        ROT3,
        ROTSWAP3,
        ROT4,
        ROTSWAP4,
        SWAP,
        SWAP2,
        SWAP3,
        SWAP4,
        PUSHUP,

        /**
         * LVT Ops
         */
        LLOAD,
        DLOAD,
        PKLOD,
        ADLOD,
        SIGLD,

        IF,
        IF_GT,
        IF_GTE,
        IF_LT,
        IF_LTE,
        CMPGT,
        CMPLT,
        CMPGTE,
        CMPLTE,
        GOTO,
        SKIP,

        EQUALS,
        RETURN_BOOL,
        AND,

        P2PKHL,
        P2PKHK,

        LVT_INIT,
        EXT;

        private byte opcode;

        Op()
        {
            this.opcode = Parameters.TOTAL_OPCODES ++;
        }
    }

    public static abstract class ScriptObject{
        public abstract byte[] getBytes() throws PayLoadException;
        public abstract ECDerivedPublicKey asPubKey() throws PayLoadException;
        public abstract ScriptObject pk2address() throws PayLoadException;
        public abstract long asLong();
        public abstract double asDouble();
        public abstract BigDecimal asVar();
        public abstract boolean asBoolean();
        public abstract ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException;
        public Address asAddress() throws PayLoadException
        {
            throw new PayLoadException("asAddress");
        }
    }

    public static class SignatureScriptObject extends ScriptObject{
        private byte[] signature;

        public SignatureScriptObject(byte[] signature)
        {
            this.signature = signature;
        }

        @Override
        public byte[] getBytes() throws PayLoadException
        {
            return signature;
        }

        @Override
        public ECDerivedPublicKey asPubKey() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert between 'signature' and 'pubkey'.");
        }

        @Override
        public ScriptObject pk2address() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert between 'signature' and 'address'.");
        }

        @Override
        public long asLong()
        {
            return 0;
        }

        @Override
        public double asDouble()
        {
            return 0;
        }

        @Override
        public BigDecimal asVar()
        {
            return new BigDecimal(new BigInteger(signature));
        }

        @Override
        public boolean asBoolean()
        {
            return false;
        }

        @Override
        public ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException
        {
            return new BooleanScriptObject(asVar().equals(scriptObject.asVar()));
        }
    }

    public static class BooleanScriptObject extends ScriptObject{
        private boolean bool;

        public BooleanScriptObject(boolean bool)
        {
            this.bool = bool;
        }

        @Override
        public byte[] getBytes() throws PayLoadException
        {
            return new byte[0];
        }

        @Override
        public ECDerivedPublicKey asPubKey() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert between 'boolean' and PubKey");
        }

        @Override
        public ScriptObject pk2address() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert 'boolean' to Address");
        }

        @Override
        public long asLong()
        {
            return bool ? 1 : 0;
        }

        @Override
        public double asDouble()
        {
            return bool ? 1 : 0;
        }

        @Override
        public BigDecimal asVar()
        {
            return new BigDecimal(asLong());
        }

        @Override
        public boolean asBoolean()
        {
            return bool;
        }

        @Override
        public ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException
        {
            return new BooleanScriptObject(scriptObject.asBoolean() == bool);
        }
    }

    public static class AddressScriptObject extends ScriptObject{
        private Address address;

        public AddressScriptObject(Address address)
        {
            this.address = address;
        }

        @Override
        public byte[] getBytes() throws PayLoadException
        {
            return address.getBytes();
        }

        @Override
        public ECDerivedPublicKey asPubKey() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert between 'address' and 'pubkey'");
        }

        @Override
        public ScriptObject pk2address() throws PayLoadException
        {
            throw new PayLoadException("Already an address.");
        }

        @Override
        public Address asAddress() throws PayLoadException
        {
            return address;
        }

        @Override
        public long asLong()
        {
            return 0;
        }

        @Override
        public double asDouble()
        {
            return 0;
        }

        @Override
        public BigDecimal asVar()
        {
            return null;
        }

        @Override
        public boolean asBoolean()
        {
            return false;
        }

        @Override
        public ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException
        {
            return new BooleanScriptObject(address.equals(scriptObject.asAddress()));
        }
    }

    public static class PubKeyScriptObject extends ScriptObject{
        private ECDerivedPublicKey publicKey;

        public PubKeyScriptObject(ECDerivedPublicKey publicKey)
        {
            this.publicKey = publicKey;
        }

        @Override
        public byte[] getBytes() throws PayLoadException
        {
            return publicKey.getBytes();
        }

        @Override
        public ECDerivedPublicKey asPubKey() throws PayLoadException
        {
            return publicKey;
        }

        @Override
        public ScriptObject pk2address() throws PayLoadException
        {
            return new AddressScriptObject(publicKey.toAddress());
        }

        @Override
        public long asLong()
        {
            return 0;
        }

        @Override
        public double asDouble()
        {
            return 0;
        }

        @Override
        public BigDecimal asVar()
        {
            return null;
        }

        @Override
        public boolean asBoolean()
        {
            return false;
        }

        @Override
        public ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException
        {
            return new BooleanScriptObject(asPubKey().equals(scriptObject.asPubKey()));
        }
    }

    public static class VarScriptObject extends ScriptObject{
        private BigDecimal data;

        public VarScriptObject(BigDecimal v)
        {
            this.data = v;
        }

        public VarScriptObject(long v)
        {
            this.data = new BigDecimal(v);
        }

        public VarScriptObject(double v)
        {
            this.data = new BigDecimal(v);
        }

        @Override
        public byte[] getBytes() throws PayLoadException
        {
            return data.toBigInteger().toByteArray();
        }

        @Override
        public ECDerivedPublicKey asPubKey() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert 'var' to pubkey (0)");
        }

        @Override
        public ScriptObject pk2address() throws PayLoadException
        {
            throw new PayLoadException("Cannot convert 'var' to pubkey (1)");
        }

        @Override
        public long asLong()
        {
            return data.longValue();
        }

        @Override
        public double asDouble()
        {
            return data.doubleValue();
        }

        @Override
        public BigDecimal asVar()
        {
            return data;
        }

        @Override
        public boolean asBoolean()
        {
            return data.longValue() > 0;
        }

        @Override
        public ScriptObject Equals(ScriptObject scriptObject) throws PayLoadException
        {
            return new BooleanScriptObject(data.equals(scriptObject.asVar()));
        }
    }

    public static byte[] P2PKH_lock(Address owner)
    {
        /**
         * Single signature Pay To Public Key-Hash
         * Stack[tx.Sig, tx.PublicKey]
         * -OP_DUP : Stack[tx.Sig, tx.PublicKey, PublicKey]
         * -OP_PK2A: Stack[tx.Sig, tx.PublicKey, Address]
         * -OP_PSHD: Stack[tx.Sig, tx.PublicKey, Address, Address]
         * -OP_EQUALS: Stack[tx.Sig, tx.PublicKey, BOOLEAN]
         * -OP_ROT: Stack[BOOLEAN, tx.PublicKey, tx.Sig]
         * -OP_CHECKSIG: Stack[BOOLEAN, BOOLEAN]
         * -OP_AND: Stack[BOOLEAN]
         * -OP_RETURN_BOOL: Stack[] -> executor{return BOOLEAN}
         //         * -ROTSWAP: Stack[BOOLEAN, tx.PublicKey, tx.Sig] -> Stack[BOOLEAN, tx.Sig, tx.PublicKey]
         */
        return ByteUtil.concatenate(new byte[]{Op.DUP.opcode, Op.PK2ADDRS.opcode, Op.PUSH_AD.opcode}, owner.getBytes(),
                new byte[]{Op.EQUALS.opcode, Op.ROT.opcode, Op.CHECKSIG.opcode,
                        Op.AND.opcode,
                        Op.RETURN_BOOL.opcode});
    }

    public static byte[] P2PKH_key(Signature signature)
    {
        return ByteUtil.concatenate(new byte[]{Op.PUSH_SIG.opcode, (byte) signature.getSignature().length}, signature.getSignature(),
                new byte[]{Op.PUSH_PK.opcode}, signature.getSignature());
    }

    /**
     * @param numSigners The number of signers
     * @param minNumSignersToValidate The minimum number of signatures to unlock the UTXO
     * @param signers An array of addresses that should sign to unlock the UTXO.
     * @return A valid multisig script.
     * @throws IOException
     */
    public static byte[] MULTISIG/**P2PKH**/(int numSigners, int minNumSignersToValidate, Address signers[]) throws IOException
    {
        /**
         * Multi signature Pay To Public Key-Hash
         * Stack[tx.Sig, tx.PublicKey, ..]
         *
         * -OP_DUP : Stack[tx.Sig, tx.PublicKey, PublicKey]
         * -OP_PK2A: Stack[tx.Sig, tx.PublicKey, Address]
         * -OP_PSHD: Stack[tx.Sig, tx.PublicKey, Address, Address]
         * -OP_EQUALS: Stack[tx.Sig, tx.PublicKey, BOOLEAN]
         * -OP_ROT: Stack[BOOLEAN, tx.PublicKey, tx.Sig]
         * -OP_CHECKSIG: Stack[BOOLEAN, BOOLEAN]
         * -OP_AND: Stack[BOOLEAN]
         * -OP_ROTSWAP: Stack[BOOLEAN, tx.Sig, tx.PublicKey]
         *
         * ..repeat..
         *
         * -OP_ADD: Stack[BOOLEAN] --Add the remainder booleans (false = 0, true = 1)
         * -OP_PUSH_LONG: minNumSignersToValidate Stack[LONG, LONG]
         * -OP_CMPGTE: Stack[BOOLEAN]
         * -OP_RETURN_BOOL: Stack[] -> executor{return BOOLEAN}
         //         * -ROTSWAP: Stack[BOOLEAN, tx.PublicKey, tx.Sig] -> Stack[BOOLEAN, tx.Sig, tx.PublicKey]
         */

        ByteArrayOutputStream ops   = new ByteArrayOutputStream();
        DataOutputStream stream     = new DataOutputStream(ops);

//        stream.write(Op.DUP.opcode);
//        stream.write(Op.PK2ADDRS.opcode);
//        stream.write(Op.PUSH_AD.opcode);
//        stream.write(Op.EQUALS.opcode);
//        stream.write(Op.ROT.opcode);
//        stream.write(Op.CHECKSIG.opcode);
//        stream.write(Op.ADD.opcode);

        /**
         * -- Current Stack --
         * Stack[tx.Sig, tx.PublicKey, ..]
         */
        for (int signer = 0; signer < numSigners; signer ++)
        {
            /**
             * Duplicate the top of the stack.
             */
            stream.write(Op.DUP.opcode);
            /**
             * Convert to public address.
             */
            stream.write(Op.PK2ADDRS.opcode);
            /**
             * Push an address to the top of the stack.
             */
            stream.write(Op.PUSH_AD.opcode);
            stream.write(signers[signer].getAddress());
            /**
             * Push a boolean to the top of the stack as a result of equals(address_a, address_b).
             */
            stream.write(Op.EQUALS.opcode);
            /**
             * Rotate the top 3 elements of the stack.
             */
            stream.write(Op.ROT.opcode);
            /**
             * Check signature is valid
             */
            stream.write(Op.CHECKSIG.opcode);
            /**
             * AND the top two elements of the stack (boolean)
             */
            stream.write(Op.ADD.opcode);
            /**
             * Rotate the top 3 elements of stack then rotate the top 2
             */
            stream.write(Op.ROTSWAP.opcode);
        }

        for (int signer = 0; signer < numSigners - 1; signer ++)
            /**
             * Add together the top 2 elements of the stack (bool(false=0 true=1) + bool(false=0 true=1))
             */
            stream.write(Op.ADD.opcode);

        stream.write(Op.PUSH_L.opcode);
        stream.write(ByteUtil.encode(minNumSignersToValidate));
        stream.write(Op.CMPGTE.opcode);

        stream.flush();
        stream.close();
        ops.flush();
        ops.close();

        return ops.toByteArray();
    }

    public static boolean execute(byte codes[], byte transaction[]) throws PayLoadException
    {
        Stack<ScriptObject> stack = new Stack<>();
        ScriptObject[]      localVarTable;

        for (int i = 0; i < codes.length; i ++)
        {
            Op code = Op.values()[codes[i]];

            switch (code)
            {
                /** push private key **/
                case PUSH_PRK:
                    break;
                case PUSH_SIG:
                    int length = Byte.toUnsignedInt(codes[++ i]);
                    byte sig_bytes[] = new byte[length];
                    for (int b = 0; b < length; b ++)
                        sig_bytes[b] = codes[b + 1];
                    i += length;
                    stack.push(new SignatureScriptObject(sig_bytes));
                    break;
                case PUSH_DATA:
                    byte[] _int_ = new byte[4];
                    for (int x = 0; x < 4; x ++)
                        _int_[x] = codes[x + 1];

                    i += 4;
                    byte array[] = new byte[ByteUtil.decodei(_int_)];
                    for (int b = 0; b < array.length; b ++)
                        array[b] = codes[b + 1];

                    stack.push(new SignatureScriptObject(array));
                    break;
                case POP:
                    stack.pop();
                    break;
                case POP2:
                    stack.pop();
                    stack.pop();
                    break;
                case POP3:
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    break;
                case POP4:
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    break;
                case POP5:
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    break;
                case POP6:
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    stack.pop();
                    break;
                case DUP2:
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    break;
                case DUP3:
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    break;
                case DUP4:
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    break;
                case DUP5:
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    break;
                case DUP6:
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    stack.push(stack.peek());
                    break;
                case PUSHUP:
                    byte[] int_ = new byte[4];
                    for (int x = 0; x < 4; x ++)
                        int_[x] = codes[x + 1];

                    i += 4;
                    stack.push(stack.get(ByteUtil.decodei(int_)));
                    break;
                case ROT2:
                {
                    ScriptObject a0 = stack.pop();
                    ScriptObject a1 = stack.pop();
                    ScriptObject a2 = stack.pop();
                    ScriptObject a3 = stack.pop();

                    stack.push(a2);
                    stack.push(a3);
                    stack.push(a1);
                    stack.push(a0);
                }
                    break;
                case ROT3:
                {
                    ScriptObject a0 = stack.pop();
                    ScriptObject a1 = stack.pop();
                    ScriptObject a2 = stack.pop();
                    ScriptObject a3 = stack.pop();
                    ScriptObject a4 = stack.pop();
                    ScriptObject a5 = stack.pop();

                    stack.push(a4);
                    stack.push(a5);
                    stack.push(a3);
                    stack.push(a2);
                    stack.push(a1);
                    stack.push(a0);
                }
                    break;
                case ROT4:
                {
                    ScriptObject a0 = stack.pop();
                    ScriptObject a1 = stack.pop();
                    ScriptObject a2 = stack.pop();
                    ScriptObject a3 = stack.pop();
                    ScriptObject a4 = stack.pop();
                    ScriptObject a5 = stack.pop();
                    ScriptObject a6 = stack.pop();
                    ScriptObject a7 = stack.pop();

                    stack.push(a6);
                    stack.push(a7);
                    stack.push(a5);
                    stack.push(a4);
                    stack.push(a3);
                    stack.push(a2);
                    stack.push(a1);
                    stack.push(a0);
                }
                break;
                case CMPGT:
                    stack.push(new BooleanScriptObject(stack.pop().asVar().compareTo(stack.pop().asVar()) < 0));
                    break;
                case CMPGTE:
                    stack.push(new BooleanScriptObject(stack.pop().asVar().compareTo(stack.pop().asVar()) <= 0));
                    break;
                case CMPLT:
                    stack.push(new BooleanScriptObject(stack.pop().asVar().compareTo(stack.pop().asVar()) > 0));
                    break;
                case CMPLTE:
                    stack.push(new BooleanScriptObject(stack.pop().asVar().compareTo(stack.pop().asVar()) >= 0));
                    break;
                case AND:
                    stack.push(new BooleanScriptObject(stack.pop().asBoolean() && stack.pop().asBoolean()));
                    break;
                case CAST_INTGR:
                    stack.push(new VarScriptObject(stack.pop().asLong()));
                    break;
                case PUSH_L:
                    byte lbytes[] = new byte[8];
                    for (int b = 0; b < 25; b ++)
                        lbytes[b] = codes[b + 1];
                    i += 8;

                    stack.push(new VarScriptObject(ByteUtil.decode(lbytes)));
                    break;
//                case PUSH_D:
//                    byte dbytes[] = new byte[8];
//                    for (int b = 0; b < 25; b ++)
//                        lbytes[b] = codes[b + 1];
//                    i += 8;
//
//                    stack.push(new VarScriptObject(ByteUtil.decode(lbytes)));
//                    break;
                case LVT_INIT:
                    localVarTable = new ScriptObject[256];
                    break;
                case P2PKHL:{
                    byte address_bytes[] = new byte[25];
                    for (int b = 0; b < 25; b ++)
                        address_bytes[b] = codes[b + 1];

                    i += 25;
                    Address address = new Address(address_bytes);

                    byte codes_b[] = P2PKH_lock(address);

                    stack.push(new BooleanScriptObject(execute(codes_b, transaction)));
                }
                break;
                case P2PKHK:{
                    byte address_bytes[] = new byte[25];
                    for (int b = 0; b < 25; b ++)
                        address_bytes[b] = codes[b + 1];

                    i += 25;
                    Address address = new Address(address_bytes);

//                    byte codes_b[] = P2PKH_key(address);

//                    stack.push(new BooleanScriptObject(execute(codes_b, transaction)));
                }
                break;
                case ROTSWAP:{
                    ScriptObject C = stack.pop();
                    ScriptObject B = stack.pop();
                    ScriptObject A = stack.pop();

                    stack.push(C);
                    stack.push(A);
                    stack.push(B);
                }
                    break;
                case ROTSWAP2:{
                    ScriptObject F = stack.pop();
                    ScriptObject D = stack.pop();
                    ScriptObject C = stack.pop();
                    ScriptObject B = stack.pop();
                    ScriptObject A = stack.pop();

                    stack.push(F);
                    stack.push(D);
                    stack.push(C);
                    stack.push(A);
                    stack.push(B);
                }
                break;
                case ROTSWAP3:{
                    ScriptObject H = stack.pop();
                    ScriptObject G = stack.pop();
                    ScriptObject F = stack.pop();
                    ScriptObject D = stack.pop();
                    ScriptObject C = stack.pop();
                    ScriptObject B = stack.pop();
                    ScriptObject A = stack.pop();

                    stack.push(H);
                    stack.push(G);
                    stack.push(F);
                    stack.push(D);
                    stack.push(C);
                    stack.push(A);
                    stack.push(B);
                }
                break;
                case ROTSWAP4:{
                    ScriptObject J = stack.pop();
                    ScriptObject I = stack.pop();
                    ScriptObject H = stack.pop();
                    ScriptObject G = stack.pop();
                    ScriptObject F = stack.pop();
                    ScriptObject D = stack.pop();
                    ScriptObject C = stack.pop();
                    ScriptObject B = stack.pop();
                    ScriptObject A = stack.pop();

                    stack.push(J);
                    stack.push(I);
                    stack.push(H);
                    stack.push(G);
                    stack.push(F);
                    stack.push(D);
                    stack.push(C);
                    stack.push(A);
                    stack.push(B);
                }
                break;
                case ROT:{
                    ScriptObject C = stack.pop();
                    ScriptObject B = stack.pop();
                    ScriptObject A = stack.pop();

                    stack.push(C);
                    stack.push(B);
                    stack.push(A);
                }
                    break;
                case DUP:
                    stack.push(stack.peek());
                    break;
                case PUSH_AD:
                    byte address_bytes[] = new byte[25];
                    for (int b = 0; b < 25; b ++)
                        address_bytes[b] = codes[b + 1];

                    i += 25;
                    Address address = new Address(address_bytes);
                    stack.push(new AddressScriptObject(address));
                    break;
                case PUSH_PK:
                    byte pubkey_bytes[] = new byte[33];
                    for (int b = 0; b < 33; b ++)
                        pubkey_bytes[b] = codes[b + 1];

                    i += 33;

                    ECDerivedPublicKey publicKey = null;
                    try
                    {
                        publicKey = new ECDerivedPublicKey(pubkey_bytes);
                    } catch (ECLibException e)
                    {
                        throw new PayLoadException("Cannot recover PubKey from bytes \n" + e.getMessage());
                    }
                    stack.push(new PubKeyScriptObject(publicKey));
                    break;
                case RETURN_BOOL:
                    if (stack.size() > 1) throw new PayLoadException("Cannot exit program, current stack size is too large.");
                    else if (stack.size() == 0) throw new PayLoadException("Stack is empty, cannot return 'boolean'");
                    return stack.pop().asBoolean();
                case CHECKSIG:
                    ScriptObject pubKeyScriptObject = stack.pop();

                    try
                    {
                        stack.push(new BooleanScriptObject(pubKeyScriptObject.asPubKey().verify(stack.pop().getBytes(), HashUtil.applySha3(transaction))));
                    } catch (ECLibException e)
                    {
                        throw new PayLoadException("Cannot verify signature with PubKey \n" + e.getMessage());
                    }
                    break;
                case EQUALS:
                    stack.push(stack.pop().Equals(stack.pop()));
                    break;
                case PK2ADDRS:
                    stack.push(stack.pop().pk2address());
                    break;
                case ADD:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

                    stack.push(new VarScriptObject(a.asVar().add(b.asVar())));
                    }break;
                case SUB:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

                    stack.push(new VarScriptObject(a.asVar().subtract(b.asVar())));
                    }break;
                case MUL:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

                    stack.push(new VarScriptObject(a.asVar().multiply(b.asVar())));
                    }break;
                case DIV:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

                    stack.push(new VarScriptObject(a.asVar().divide(b.asVar())));
                    }break;
                case MOD:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

                    stack.push(new VarScriptObject(a.asLong() % b.asLong()));
                    }break;
                case SIN:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

//                    stack.push(new VarScriptObject(a.asVar().divide(b.asVar())));
                    }break;
                case COS:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

//                    stack.push(new VarScriptObject(a.asVar().divide(b.asVar())));
                    }break;
                case TAN:{
                    ScriptObject b = stack.pop();
                    ScriptObject a = stack.pop();

//                    stack.push(new VarScriptObject(a.asVar().divide(b.asVar())));
                    }break;
            }
        }

        return false;
    }
}