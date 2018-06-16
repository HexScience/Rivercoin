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

package com.riverssen.core.security;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.transactions.UnspentTransaction;

import java.util.*;

public class UTXOTree {
    private static HashMap<PublicAddress, List<UnspentTransaction>> utxoMap = new HashMap<>();
    private static Set<UnspentTransaction>                          utxoSet = new HashSet<>();

    public static void add(UnspentTransaction utxo)
    {
        if(utxoSet.contains(utxo)) return;

        utxoSet.add(utxo);

        List<UnspentTransaction> list = utxoMap.get(utxo.receiver());

        if(list == null)
        {
            list = new ArrayList<>();
            list.add(utxo);

            utxoMap.put(utxo.receiver(), list);
        } else list.add(utxo);
    }

    public static RiverCoin traverse(PublicAddress publicAddress) {
        return null;
    }
}
