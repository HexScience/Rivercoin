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

package com.riverssen.utils;

import com.riverssen.core.headers.*;
import com.riverssen.core.transactions.UTXO;

import java.util.List;

public class UTXOTraverser<T extends Encodeable & JSONFormattable & Exportable>
{
    private T t;

    void traverse(List<UTXO> list)
    {
        for(UTXO utxo : list)
        {
        }
    }

    void traverse(List<TransactionI> tokenList, List<UTXO<T>> out)
    {
        for (TransactionI transactionI : tokenList)
            for (UTXO utxo : transactionI.getTXIDs())
                out.add(utxo);
    }

    T    get()
    {
        return t;
    }
}
