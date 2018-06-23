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

package com.riverssen.riverssen;

import com.riverssen.core.transactions.TransactionOutput;

import java.util.*;

public class ImmutableRiverFlowMap implements UTXOMap
{
    private HashMap<String, Set<TransactionOutput>> data;

    public ImmutableRiverFlowMap()
    {
        this.data = new HashMap<>();
    }

    public ImmutableRiverFlowMap(HashMap<String, Set<TransactionOutput>> data)
    {
        this.data = data;
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
    @Constant
    public Set<TransactionOutput> get(String address)
    {
        if(data.containsKey(address)) return data.get(address);

        data.put(address, new LinkedHashSet<>());

        return data.get(address);
    }
}