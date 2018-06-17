package com.riverssen.core.headers;

import com.riverssen.core.FullBlock;

public interface BlockChainI extends Runnable
{
    public
    void FetchTransactions();
    void ValidateTransactions();
    void RemoveDoubleSpends();
    void LoadBlockChain();
    void FetchBlockChainFromPeers();
    void FetchBlockChainFromDisk();
    void Validate();
    long currentBlock();

    void queueBlock(FullBlock block);
}