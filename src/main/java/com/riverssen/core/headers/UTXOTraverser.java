package com.riverssen.core.headers;

import com.riverssen.core.transactions.UTXO;

import java.util.List;

public class UTXOTraverser<T extends Encodeable & JSONFormattable>
{
    private T t;

    void traverse(List<TransactionI> tokenList, List<UTXO<T>> out)
    {
//        for(TransactionI transactionI : tokenList)
//            if(transactionI.matches());
    }

    T    get()
    {
        return t;
    }
}
