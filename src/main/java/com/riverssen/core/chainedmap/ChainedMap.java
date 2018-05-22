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

package com.riverssen.core.chainedmap;

import com.riverssen.core.chain.BlockHeader;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Base58;
import com.riverssen.utils.ByteUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class ChainedMap implements Encodeable
{
    static interface CallBack<K, V>{
        void onEvent(K k, V v);
    }

    class Element implements Comparable<Element>{
        private byte        value[];
        Element             left;
        Element             right;

        public Element(Element right, Element left)
        {
            this.left   = left;
            this.right  = right;
        }

        public Element(byte key[])
        {
            this.value = key;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        public byte[] gethash()
        {
            if(left == null) return value;

            return ByteUtil.concatenate(left.gethash(), right.gethash());
        }

        @Override
        public int compareTo(Element o) {
            return new BigInteger(gethash()).compareTo(new BigInteger(o.gethash()));
        }
    }

    HashMap<PublicAddress, Set<byte[]>>     elements;
    Set<byte[]>                             allElements;
    String                                  roothash;

    public boolean get(byte value[])
    {
        return this.allElements.contains(value);
    }

    public boolean add(PublicAddress address, byte value[])
    {
        if(get(value)) return false;

        this.allElements.add(value);
        if(this.elements.containsKey(address))
            this.elements.get(address).add(value);
        else {
            this.elements.put(address, new HashSet<>());
            this.elements.get(address).add(value);
        }
        return true;
    }

    private Element createMerkleTree(PublicAddress address)
    {
        PriorityQueue<Element> hashes = new PriorityQueue<>();

        for(byte array[] : elements.get(address)) hashes.add(new Element(array));

        while(hashes.size() > 1)
            hashes.add(new Element(hashes.poll(), hashes.poll()));

        return hashes.poll();
    }

    /** This will return a merkle tree where if a utxo is invalid the public address associated with it will be invalidated; therefore the root of the problem will be found in the .. ehm, merkle... root. **/
    public String calculateHash()
    {
        PriorityQueue<Element> hashes = new PriorityQueue<>();

        for(PublicAddress address : elements.keySet())
            hashes.add(createMerkleTree(address));

        while(hashes.size() > 1)
            hashes.add(new Element(hashes.poll(), hashes.poll()));

        roothash = Base58.encode(hashes.poll().gethash());

        return roothash;
    }

    public boolean remove(byte value[])
    {
        if(!get(value)) return false;

        this.allElements.remove(new Element(value));
        return true;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    public long validate(BlockHeader header)
    {
        return 0;
    }
}