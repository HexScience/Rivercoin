package com.riverssen.protobufs;

public interface ProtoBuf
{
    void export(ProtobufOutputStream stream);
    void readin(ProtobufInputStream stream);
}