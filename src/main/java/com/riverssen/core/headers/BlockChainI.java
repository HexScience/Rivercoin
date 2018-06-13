package com.riverssen.core.headers;

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
}