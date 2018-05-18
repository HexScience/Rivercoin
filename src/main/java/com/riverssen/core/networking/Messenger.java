package com.riverssen.core.networking;

import com.riverssen.core.FullBlock;
import com.riverssen.core.chain.Block;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.tokens.Token;

public interface Messenger
{
    void        sendToken(TransactionI token);
    Token       receiveToken();

    void        sendBlock(FullBlock block);
    Block       receiveBlock();

    void        sendSolution(Solution solution);
    Solution    receiveSolution();

    void        send();
    void        receive();
}