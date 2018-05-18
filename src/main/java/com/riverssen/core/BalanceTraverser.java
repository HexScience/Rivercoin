package com.riverssen.core;

import com.riverssen.core.headers.UTXOTraverser;
import com.riverssen.core.tokens.Token;

import java.math.BigInteger;
import java.util.List;

public class BalanceTraverser implements UTXOTraverser<RiverCoin>
{
    private BigInteger balance;

    @Override
    public void traverse(List<Token> tokenList)
    {
        for(Token token : tokenList)
        {
        }
    }

    @Override
    public RiverCoin get()
    {
        return new RiverCoin(balance);
    }
}
