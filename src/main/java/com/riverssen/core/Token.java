package com.riverssen.core;

import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;
import com.riverssen.utils.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Token extends Comparable<Token>
{
    boolean           valid();
    void              write(DataOutputStream stream) throws IOException;
    static Token      read(DataInputStream stream)
    {
        return null;
    }

    long              getTimeStamp();
    CompressedAddress getSender();
    PublicAddress     getReceiver();
    int               getNonce();
    int               hashCode();

    default int compareTo(Token token)
    {
        return getTimeStamp() > token.getTimeStamp() ? 1 : -1;
    }
}