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

import com.riverssen.core.transactions.TransactionOutput;
import com.riverssen.core.utils.Tuple;

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
    public UTXOMap getBranch() {
        return new ImmutableRiverFlowMap(data);
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
    public void addAll(UTXOMap map) {
        if(map instanceof RiverFlowMap)
            data.putAll(((RiverFlowMap) map).data);
        else {
            ImmutableRiverFlowMap map_ = (ImmutableRiverFlowMap) map;

            for(String remove : map_.remove)
                remove(remove);

            for(Tuple<String, TransactionOutput> remove : map_.remove_values)
                remove(remove.getI(), remove.getJ());

            for(Tuple<String, TransactionOutput> add : map_.add)
                add(add.getI(), add.getJ());
        }
    }

    @Override
    public Set<TransactionOutput> get(String address)
    {
        if(data.containsKey(address)) return data.get(address);

        data.put(address, new LinkedHashSet<>());

        return data.get(address);
    }
}