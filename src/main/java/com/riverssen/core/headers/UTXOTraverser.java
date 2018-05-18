package com.riverssen.core.headers;

import java.util.List;

public interface UTXOTraverser<T>
{
    void traverse(List<TransactionI> tokenList);
    T    get();
}
