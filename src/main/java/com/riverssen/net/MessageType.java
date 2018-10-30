package com.riverssen.net;

public enum MessageType
{
    PING, PONG,

    GETBLOCK,
    GETBLOCKHEADER,
    GETCHAIN,
    GETCHAINHEADERS,
    GETCHAINSIZE,

    BLOCK,
    BLOCKHEADER,
    CHAIN,
    CHAINHEADERS,
    CHAINSIZE,
    TRANSACTION,
}