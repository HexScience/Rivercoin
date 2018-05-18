package com.riverssen.core.chain;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Serialisable
{
    public void serialize(DataOutputStream stream) throws Exception;
    public void deserialize(DataInputStream stream, String version) throws Exception;
}
