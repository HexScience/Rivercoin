package com.riverssen.core.headers;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface UTXO<T>
{
    void store(DataOutputStream stream);
    void load (DataInputStream stream);

    T    get();

    String toString();
}