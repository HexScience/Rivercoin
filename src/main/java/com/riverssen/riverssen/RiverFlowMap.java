/**
 * Copyright 2018 Ragnarr Ivarssen
 * This Software Is Free For Use And Must
 *
 * Not Be Sold And Or Distributed Without The Written Permission Of
 * (Ragnarr Ivarssen Riverssen@gmail.com).
 *
 * The Software's Code Must Not Be Made Public, The Software Must Not Be Decompiled, Reverse Engineered, Or Unobfuscated In Any Way
 * Without The Written Permission Of (Ragnarr Ivarssen Riverssen@gmail.com).
 *
 * The Creator (Ragnarr Ivarssen Riverssen@Gmail.com) Does Not Provide Any Warranties
 * To The Quality Of The Software And It Is Provided "As Is".
 */

package com.riverssen.riverssen;

import com.riverssen.core.security.PublicAddress;
import com.riverssen.core.transactions.TransactionOutput;
import com.riverssen.core.transactions.UnspentTransaction;

import java.util.*;

public class RiverFlowMap implements UTXOMap
{
    private HashMap<String, Set<TransactionOutput>> data;

    public RiverFlowMap()
    {
        this.data = new HashMap<>();
    }

    @Override
    public void add(String publicAddress, TransactionOutput utxo) {
        get(publicAddress).add(utxo);
    }

    @Override
    public void addAll(String publicAddress, List<TransactionOutput> utxos) {
        Set<TransactionOutput> set = get(publicAddress);

        for(TransactionOutput utxo : utxos)
            set.add(utxo);
    }

    @Override
    public void addAll(List<TransactionOutput> utxos) {
        for(TransactionOutput output : utxos)
            add(output.getOwner().toString(), output);
    }

    @Override
    public void remove(String publicAddress) {
        data.remove(publicAddress);
    }

    @Override
    public void remove(String publicAddress, TransactionOutput utxo) {
        get(publicAddress).remove(utxo);
    }

    @Override
    @Deprecated
    public void remove(TransactionOutput utxo) {
        for(Set<TransactionOutput> set : data.values())
            if(set.contains(utxo))
                set.remove(utxo);
    }


    private Element createMerkleTree(String address)
    {
        PriorityQueue<Element> hashes = new PriorityQueue<>();

        for(TransactionOutput array : data.get(address)) hashes.add(new Element(array));

        while(hashes.size() > 1)
            hashes.add(new Element(hashes.poll(), hashes.poll()));

        return hashes.poll();
    }

    @Override
    public byte[] getStamp() {
        PriorityQueue<Element> hashes = new PriorityQueue<>();

        for(String address : data.keySet())
            hashes.add(createMerkleTree(address));

        while(hashes.size() > 1)
            hashes.add(new Element(hashes.poll(), hashes.poll()));

        return new byte[0];
    }

    @Override
    public Set<TransactionOutput> get(String address)
    {
        if(data.containsKey(address)) return data.get(address);

        data.put(address, new LinkedHashSet<>());

        return data.get(address);
    }
}