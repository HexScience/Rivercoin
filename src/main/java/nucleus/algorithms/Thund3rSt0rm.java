package nucleus.algorithms;

import nucleus.util.Base58;
import nucleus.util.HashUtil;

import java.util.HashMap;
import java.util.Map;

public class Thund3rSt0rm implements HashAlgorithm
{
    private final static HashAlgorithm[] algorithms = {
            new Blake(), new Gost(), new Keccak512(),
            new Skein_512_512(), new Sha512()};


    @Override
    public byte[] encode(byte[] data) {
        /** generate a pseudorandom seed from pingonging 8 bytes from the data set**/
//        byte[] seed = ByteUtil.pingpong(data, 8);

        byte[] sha256 = new Sha256().encode(data);

        Map<Integer, HashAlgorithm> algos = new HashMap<>();
        algos.put(0, algorithms[0]);
        algos.put(1, algorithms[1]);
        algos.put(2, algorithms[2]);
        algos.put(3, algorithms[3]);
        algos.put(4, algorithms[4]);

        for (int i = 0; i < 32; i ++)
            if (algos.containsKey(sha256[30] % 5))
                sha256 = HashAlgorithm.xorif(32, algos.get(sha256[30] % 5).encode(sha256));

        return sha256;
    }

    @Override
    public String encode16(byte[] data) {
        return HashUtil.hashToStringBase16(encode(data));
    }

    @Override
    public String encode32(byte[] data) {
        return HashUtil.base36Encode(encode(data));
    }

    @Override
    public String encode58(byte[] data) {
        return Base58.encode(encode(data));
    }

    @Override
    public String encode64(byte[] data) {
        return HashUtil.base64StringEncode(encode(data));
    }
}
