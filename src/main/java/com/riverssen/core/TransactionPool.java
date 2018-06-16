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

package com.riverssen.core;

import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.headers.ContextI;

import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class TransactionPool
{
    private Set<TransactionI>   pool;
    private ContextI            context;
    private long                lastTransactionTime;

    public TransactionPool(ContextI network)
    {
        pool = new LinkedHashSet<>();
        context = network;
    }

    public void addInternal(TransactionI token)
    {
        /** check tokens timestamp isn't older than a block **/
        if(System.currentTimeMillis() - token.getTimeStamp() > (context.getConfig().getAverageBlockTime() * 1.5)) return;

        lastTransactionTime = System.currentTimeMillis();

        pool.add(token);

        context.getNetworkManager().broadCastNewTransaction(token);
    }

    public void addRelayed(TransactionI transaction)
    {
        /** check tokens timestamp isn't older than a block **/
        if(System.currentTimeMillis() - transaction.getTimeStamp() > (context.getConfig().getAverageBlockTime() * 1.5)) return;

        lastTransactionTime = System.currentTimeMillis();

        pool.add(transaction);
    }

    public boolean available()
    {
        return pool.size() > 0;
    }

    public TransactionI next()
    {
        TransactionI txi = pool.iterator().next();
        pool.remove(txi);
        return txi;
    }

    public boolean getLastTransactionWas(long time) {
        return System.currentTimeMillis() - lastTransactionTime >= time;
    }
}