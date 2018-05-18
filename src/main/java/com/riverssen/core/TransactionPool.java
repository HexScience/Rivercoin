package com.riverssen.core;

import com.riverssen.core.headers.Transaction;
import com.riverssen.core.networking.PeerNetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class TransactionPool
{
    private PriorityQueue<Transaction> pool;
    private Set<Transaction>           hset;
    private PeerNetwork          pnet;

    public TransactionPool(PeerNetwork network)
    {
        pool = new PriorityQueue<>();
        hset = Collections.synchronizedSet(new HashSet<>());
        pnet = network;
    }

    public void add(Transaction token)
    {
        /** check token doesn't already exist in pool */
        if(hset.contains(token)) return;

        /** check tokens timestamp isn't older than a block **/
        if(System.currentTimeMillis() - token.getTimeStamp() > (450000)) return;

        pool.add(token);
        hset.add(token);

        pnet.BroadCastNewTransaction(token);
    }
}