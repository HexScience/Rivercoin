package com.riverssen.core.chain;

import com.riverssen.core.Config;
import com.riverssen.core.tokens.Token;
import com.riverssen.utils.HashUtil;
import com.riverssen.utils.Tuple;

import java.util.concurrent.Callable;

public class MineTask implements Callable<Tuple<Long, String>>
{
    private final Token token;
    private String      hash;
    private long        start;
    private long        end;

    /**
     * @param start inclusive start
     * @param end   inclusive end
     */
    public MineTask(String lastHash, long start, long end, Token token)
    {
//        this.block = block.getByteBufferCopy();
//        this.hash  = block.getHash();
        this.start  = start;
        this.end    = end;
        this.hash   = lastHash;
        this.token  = token;
    }

    @Override
    public Tuple<Long, String> call() throws Exception
    {
        long nonce = start - 1;

        String hash = this.hash;
        String target = HashUtil.createDifficultyString();

        while (!hash.substring(0, Config.getConfig().BLOCK_MINING_DIFFICULTY).equals(target))
        {
//            System.out.println(nonce + " " + end + " " + start);
            hash = generateHash(++nonce);
            if(nonce == end)
            {
                if(!hash.substring(0, Config.getConfig().BLOCK_MINING_DIFFICULTY).equals(target)) return null;
                return new Tuple<>(nonce, hash);
            };
        }

//        String hash = token.getHashAsString(++nonce);
//        String target = HashUtil.createDifficultyString();
//
//        while (!hash.substring(0, Config.getConfig().TOKEN_MINING_DIFFICULTY).equals(target))
//            hash = token.getHashAsString(++nonce);
//
//        lastHash = hash;
//        tokens.append(token.toJSON(nonce) + "\n");
//        index++;
//
//        txio.add(token);

//        Logger.alert("block[" + hash + "] mined!");
        return new Tuple<>(nonce, hash);
    }

    public synchronized String generateHash(long i)
    {
        return token.getHashAsString(i, hash);
    }

//    public synchronized void generateHash(byte data[])
//    {
//        hash = HashUtil.hashToStringBase16(HashUtil.applySha256(data));
//    }
}
