package com.riverssen.core.networking;

import com.riverssen.core.Config;
import com.riverssen.core.RVCCore;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.tokens.Token;
import com.riverssen.utils.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class MessageWrapper
{
    private static final int TYPE_SOLUTION = 0;
    private static final int TYPE_NEWTOKEN = 1;
    private static final short MSG_HANDSHAKE = 2;

    public static synchronized void   newTokenMessage(byte[] msg, int version)
    {
        try{
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(msg));

            Token token = Token.read(stream, RVCCore.version);

            stream.close();

            if(token == null) return;

//            RVCCore.get().getChain().add(token);
        } catch (Exception e)
        {
        }
    }

    public static synchronized String tokenMessage(Token token)
    {
        return "0x" + Integer.toString(TYPE_SOLUTION, 16) + ";" + token.toMsg();
    }

    public static synchronized String solutionMessage(Solution solution)
    {
        String msg =  "0x" + Integer.toString(TYPE_SOLUTION, 16) + ";" + solution;
        return msg;
    }

    public static synchronized Solution solutionMessage(byte solution[])
    {
        return null;//new Solution(solution);
    }

    public synchronized static void decode(String msg)
    {
        if(msg.startsWith("0x0;"))
        {
            msg = msg.substring(4);
//            Solution solution = solutionMessage(msg);

//            RVCCore.get().getChain().addSolution(solution);
        }
    }

    public static byte[] wrapChainSize()
    {
        byte bytes[] = new byte[64 + 4];

        ByteBuffer chainSizeInfo = ByteBuffer.allocate(64 + 4);

        return bytes;
    }

    public static byte[] handShakeMessage()
    {
        ByteBuffer shortBuffer = ByteBuffer.allocate(56);
        shortBuffer.putShort(MSG_HANDSHAKE);
        shortBuffer.putShort(RVCCore.versionBytes);
        shortBuffer.put(new PublicAddress(Config.getConfig().PUBLIC_ADDRESS).getBytes());
        shortBuffer.put(Config.getConfig().TARGET_DIFFICULTY.toBigInteger().toByteArray());
        shortBuffer.flip();

        return shortBuffer.array();
    }
}