package com.riverssen.core.tokens;

import com.riverssen.core.security.PubKey;

public class UTFDataToken extends Token
{
    private byte msg[];

    public UTFDataToken(String sender, String receiver, String msg)
    {
        PubKey receiverKey = new PubKey(receiver);

        this.msg = receiverKey.encrypt(msg);

        setSenderAddress(sender);
        setReceiverAddress(receiver);
        setComment("");
    }
}