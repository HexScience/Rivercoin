/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.system;

import com.riverssen.core.RiverCoin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Config
{
    public long getAverageBlockTime()
    {
        /** two minutes **/
        return 120_000L;
    }

    /** calculate the cost of a contract **/
    public static String getCost(long contractSize)
    {
        return new BigDecimal(getReward()).divide(new BigDecimal(3000), 20, RoundingMode.HALF_UP).multiply(new BigDecimal(contractSize)).toPlainString();
    }

    public static String getReward()
    {
        LatestBlockInfo info = new LatestBlockInfo();
        try
        {
            info.read();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        long latestBlock =  info.getLatestBlock();
        long halfEvery   =  20000;

        BigDecimal decimal = new BigDecimal("50").divide(new BigDecimal(latestBlock).divide(new BigDecimal(halfEvery), 20, RoundingMode.HALF_DOWN).round(MathContext.DECIMAL128).multiply(new BigDecimal(2)), 20, RoundingMode.HALF_DOWN);

        return new RiverCoin(decimal).toRiverCoinString();
    }

    public BigInteger getMinimumDifficulty()
    {
        return MINIMUM_TARGET_DIFFICULTY;
    }

    private final BigInteger MINIMUM_TARGET_DIFFICULTY = new BigDecimal("225269536353234632640832032722171634457188848844000484574312395358531977087").toBigInteger();
}
