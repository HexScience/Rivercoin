package com.riverssen.core.headers;

import com.riverssen.core.tokens.Token;

import java.util.List;

public interface UTXOTraverser<T>
{
    void traverse(List<Token> tokenList);
    T    get();
}
