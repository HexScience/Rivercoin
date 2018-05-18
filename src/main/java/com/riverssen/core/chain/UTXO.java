package com.riverssen.core.chain;

import com.riverssen.core.headers.TransactionI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UTXO
{
    private Map<String, WalletOutputInput> utxoMap;

    public UTXO()
    {
        this.utxoMap = Collections.synchronizedMap(new HashMap<>());
    }

    public void add(TransactionI token)
    {
    }
}