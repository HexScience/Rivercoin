package nucleus.mining;

import com.google.common.util.concurrent.AtomicDouble;
import nucleus.system.Parameters;
import nucleus.threading.ThreadedAccess;
import nucleus.util.ByteUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;
import static nucleus.threading.Async.*;

public class MiningThread implements Runnable, ThreadedAccess
{
    private final AtomicInteger code;
    private final byte          data[];
    private final AtomicDouble  difficulty;
    private final AtomicLongArray nonce;
    private final AtomicLongArray hash;

    public MiningThread (byte data[], double difficulty) throws IOException
    {
//        long longData[] = new long[(int) Math.ceil((double)data.length / 8.0)];
//        ByteBuffer.wrap(data).asLongBuffer().get(longData);

        this.code = new AtomicInteger(NO_EXECUTE);
        this.data = data;//new AtomicLongArray(ByteUtil.concatenate(longData));
        this.difficulty = new AtomicDouble(difficulty);
        this.nonce = new AtomicLongArray(3);
        this.hash = new AtomicLongArray(4);
    }

    public int getCode()
    {
        return code.get();
    }

    public Nonce getNonce()
    {
        return new Nonce(nonce.get(0), nonce.get(1), nonce.get(2));
    }

    public long[] getHash()
    {
        return new long[] {hash.get(0), hash.get(1), hash.get(2), hash.get(3)};
    }

    @Override
    public void run()
    {
        try
        {
            code.set(PREPARING);
            NKMiner miner = new NKMiner();

            code.set(RUNNING);
            byte hash[] = miner.mine(data, Parameters.toInteger(difficulty.get()));
            this.hash.set(0, ByteUtil.decode(hash));
            this.hash.set(1, ByteUtil.decode(ByteUtil.trim(hash, 8, 16)));
            this.hash.set(2, ByteUtil.decode(ByteUtil.trim(hash, 16, 24)));
            this.hash.set(3, ByteUtil.decode(ByteUtil.trim(hash, 24, 32)));
            this.nonce.set(0, miner.getNonce().getA());
            this.nonce.set(1, miner.getNonce().getB());
            this.nonce.set(2, miner.getNonce().getC());
            code.set(SUCCESS);
        } catch (Exception e)
        {
            code.set(EXCECPTION);
            e.printStackTrace();
        }
    }

    @Override
    public <T> T blockingGet(String name)
    {
        return null;
    }

    @Override
    public <T> T Get(String name)
    {
        switch (name)
        {
            case "hash":
                return (T) getHash();
            case "nonce":
                return (T) getNonce();
            case "code":
                return (T) (Integer) getCode();

                default:
                    return null;
        }
    }
}
